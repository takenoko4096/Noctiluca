package io.github.takenoko4096.noctiluca.execute.arguments.selector

import io.github.takenoko4096.noctiluca.execute.NoctilucaCommandSourceStack
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType

class SelectorArguments(private val arguments: Set<SelectorArgument>) {
    init {
        /*val groups = arguments.groupBy { if (it is SelectorArgument.Not<*>) it.argument.id else it.id }

        for ((id, args) in groups) {
            val rule = args.first().duplication
            when (rule) {
                SelectorArgumentDuplicationRule.NEVER -> {
                    if (args.size > 1) throw IllegalArgumentException()
                }
                SelectorArgumentDuplicationRule.INVERTED_ONLY -> {

                }
            }
        }*/
    }

    fun apply(stack: NoctilucaCommandSourceStack, entities: List<Entity>): List<Entity> {
        var list = entities
        val finder = stack.copy()

        for (argument in arguments.sortedByDescending { it.priority }) {
            list = argument.apply(finder, list)
        }

        return list
    }

    fun patched(patch: SelectorArguments): SelectorArguments {
        val set = mutableSetOf<SelectorArgument>()

        val old = arguments.toMutableSet()

        for (patchArgument in patch.arguments) {
            set.add(patchArgument)
            old.removeIf { it == patchArgument }
        }

        set.addAll(old)

        return SelectorArguments(set)
    }

    class Builder(callback: Builder.() -> Unit) {
        private val arguments = mutableSetOf<SelectorArgument>()

        init {
            callback()
        }

        operator fun <T> T.not(): SelectorArgument.Not<T> where T : SelectorArgument.FilterableSelectorArgument, T : SelectorArgument.Invertible {
            return SelectorArgument.Not(this).also {
                arguments.remove(this)
                arguments.add(it)
            }
        }

        fun x(x: Double): SelectorArgument.X {
            return SelectorArgument.X(x).also { arguments.add(it) }
        }

        fun y(y: Double): SelectorArgument.Y {
            return SelectorArgument.Y(y).also { arguments.add(it) }
        }

        fun z(z: Double): SelectorArgument.Z {
            return SelectorArgument.Z(z).also { arguments.add(it) }
        }

        fun sort(sortOrder: SelectorSortOrder) {
            arguments.add(SelectorArgument.Sort(sortOrder))
        }

        fun type(type: EntityType<*>): SelectorArgument.Type {
            return SelectorArgument.Type(type).also { arguments.add(it) }
        }

        fun limit(limit: Int) {
            arguments.add(SelectorArgument.Limit(limit))
        }

        internal fun build(): SelectorArguments {
            return SelectorArguments(arguments.toSet())
        }
    }
}
