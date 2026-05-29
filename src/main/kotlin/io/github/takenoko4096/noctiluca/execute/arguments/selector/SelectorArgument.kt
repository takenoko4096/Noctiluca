package io.github.takenoko4096.noctiluca.execute.arguments.selector

import io.github.takenoko4096.noctiluca.execute.NoctilucaCommandSourceStack
import net.minecraft.world.entity.Entity

abstract class SelectorArgument(val phase: ArgumentPhase) {
    protected abstract fun modify(finder: NoctilucaCommandSourceStack, entity: Entity): Boolean

    fun apply(finder: NoctilucaCommandSourceStack, candidates: List<Entity>): List<Entity> {
        return candidates.filter {
            modify(finder, it)
        }
    }

    enum class ArgumentPhase(val priority: Int) {
        CONFIGURATION(2),
        FILTERING(1),
        LIMITATION(0)
    }

    class X(private val x: Double) : SelectorArgument(ArgumentPhase.CONFIGURATION) {
        override fun modify(finder: NoctilucaCommandSourceStack, entity: Entity): Boolean {
            finder.setPosition(finder.getPosition().apply { this.x = x })
            return true
        }
    }
}
