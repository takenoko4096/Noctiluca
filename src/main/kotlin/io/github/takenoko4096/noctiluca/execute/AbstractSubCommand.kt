package io.github.takenoko4096.noctiluca.execute

import io.github.takenoko4096.noctiluca.execute.arguments.selector.EntitySelector
import net.minecraft.world.entity.Entity

abstract class AbstractSubCommand {
    protected abstract fun apply(stack: NoctilucaCommandSourceStack): List<NoctilucaCommandSourceStack>

    abstract class Redirectable : AbstractSubCommand() {
        abstract fun redirect(stack: NoctilucaCommandSourceStack)

        override fun apply(stack: NoctilucaCommandSourceStack): List<NoctilucaCommandSourceStack> {
            val cpy = stack.copy()
            redirect(cpy)
            return listOf(cpy)
        }
    }

    abstract class Forkable(protected val entitySelector: EntitySelector) : AbstractSubCommand() {
        abstract fun fork(stack: NoctilucaCommandSourceStack, entity: Entity)

        override fun apply(stack: NoctilucaCommandSourceStack): List<NoctilucaCommandSourceStack> {
            return entitySelector.getEntities(stack).map {
                val cpy = stack.copy()
                fork(cpy, it)
                cpy
            }
        }
    }
}
