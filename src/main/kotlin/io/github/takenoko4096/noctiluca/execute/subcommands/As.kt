package io.github.takenoko4096.noctiluca.execute.subcommands

import io.github.takenoko4096.noctiluca.execute.AbstractSubCommand
import io.github.takenoko4096.noctiluca.execute.NoctilucaCommandSourceStack
import io.github.takenoko4096.noctiluca.execute.arguments.selector.EntitySelector
import net.minecraft.world.entity.Entity

class As(entitySelector: EntitySelector) : AbstractSubCommand.Forkable(entitySelector) {
    override fun fork(stack: NoctilucaCommandSourceStack, entity: Entity) {
        stack.setExecutor(entity)
    }
}
