package io.github.takenoko4096.noctiluca.execute.arguments.selector

import io.github.takenoko4096.noctiluca.execute.NoctilucaCommandSourceStack
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType

sealed class SelectorArgument(val id: String, val duplication: SelectorArgumentDuplicationRule, val priority: Int) {
    internal abstract fun apply(finder: NoctilucaCommandSourceStack, candidates: List<Entity>): List<Entity>

    abstract class ConfigurableSelectorArgument(id: String) : SelectorArgument(id, SelectorArgumentDuplicationRule.NEVER, 4) {
        abstract fun configure(finder: NoctilucaCommandSourceStack)

        override fun apply(finder: NoctilucaCommandSourceStack, candidates: List<Entity>): List<Entity> {
            configure(finder)
            return candidates
        }
    }

    abstract class SortableSelectorArgument(id: String) : SelectorArgument(id, SelectorArgumentDuplicationRule.NEVER, 3) {
        abstract fun sort(stack: NoctilucaCommandSourceStack, entity: Entity): Double

        override fun apply(finder: NoctilucaCommandSourceStack, candidates: List<Entity>): List<Entity> {
            return candidates.sortedBy { sort(finder.copy(), it) }
        }
    }

    abstract class FilterableSelectorArgument(id: String, duplication: SelectorArgumentDuplicationRule) : SelectorArgument(id, duplication, 2) {
        abstract fun filter(stack: NoctilucaCommandSourceStack, entity: Entity): Boolean

        override fun apply(finder: NoctilucaCommandSourceStack, candidates: List<Entity>): List<Entity> {
            return candidates.filter { filter(finder.copy(), it) }
        }
    }

    abstract class LimitableSelectorArgument(id: String) : SelectorArgument(id, SelectorArgumentDuplicationRule.NEVER, 1) {
        abstract fun limit(stack: NoctilucaCommandSourceStack, entities: List<Entity>): IntRange

        override fun apply(finder: NoctilucaCommandSourceStack, candidates: List<Entity>): List<Entity> {
            return candidates.slice(limit(finder.copy(), candidates))
        }
    }

    abstract class ModifiableSelectorArgument(id: String) : SelectorArgument(id, SelectorArgumentDuplicationRule.NEVER, 0) {
        abstract fun modify(stack: NoctilucaCommandSourceStack, entities: List<Entity>): List<Entity>

        override fun apply(finder: NoctilucaCommandSourceStack, candidates: List<Entity>): List<Entity> {
            return modify(finder.copy(), candidates)
        }
    }

    class X(private val x: Double) : ConfigurableSelectorArgument("x") {
        override fun configure(finder: NoctilucaCommandSourceStack) {
            finder.setPosition(finder.getPosition().also { it.x = x })
        }
    }

    class Y(private val y: Double) : ConfigurableSelectorArgument("y") {
        override fun configure(finder: NoctilucaCommandSourceStack) {
            finder.setPosition(finder.getPosition().also { it.y = y })
        }
    }

    class Z(private val z: Double) : ConfigurableSelectorArgument("z") {
        override fun configure(finder: NoctilucaCommandSourceStack) {
            finder.setPosition(finder.getPosition().also { it.z = z })
        }
    }

    class Sort(private val sortOrder: SelectorSortOrder) : SortableSelectorArgument("sort") {
        override fun sort(stack: NoctilucaCommandSourceStack, entity: Entity): Double {
            return sortOrder.getCriteria(stack, entity)
        }
    }

    class Type(private val type: EntityType<*>) : FilterableSelectorArgument("type", SelectorArgumentDuplicationRule.INVERTED_ONLY), Invertible {
        override fun filter(stack: NoctilucaCommandSourceStack, entity: Entity): Boolean {
            return entity.type == type
        }
    }

    class Name(private val name: String) : FilterableSelectorArgument("name", SelectorArgumentDuplicationRule.INVERTED_ONLY) {
        override fun filter(stack: NoctilucaCommandSourceStack, entity: Entity): Boolean {
            return entity.customName?.string == name
        }
    }

    class Limit(private val limit: Int) : LimitableSelectorArgument("limit") {
        override fun limit(stack: NoctilucaCommandSourceStack, entities: List<Entity>): IntRange {
            return 0..<limit
        }
    }

    interface Invertible

    class Not<T>(val argument: T) : FilterableSelectorArgument("not", SelectorArgumentDuplicationRule.ALWAYS) where T : FilterableSelectorArgument, T : Invertible {
        override fun filter(stack: NoctilucaCommandSourceStack, entity: Entity): Boolean {
            return !argument.filter(stack, entity)
        }
    }
}
