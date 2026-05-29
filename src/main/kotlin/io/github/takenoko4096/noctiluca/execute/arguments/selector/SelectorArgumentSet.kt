package io.github.takenoko4096.noctiluca.execute.arguments.selector

import io.github.takenoko4096.noctiluca.execute.NoctilucaCommandSourceStack
import net.minecraft.world.entity.Entity

class SelectorArgumentSet(private vararg val arguments: SelectorArgument) {
    fun apply(stack: NoctilucaCommandSourceStack, entities: List<Entity>): List<Entity> {
        var list = entities
        val finder = stack.copy()

        for (argument in arguments.sortedByDescending { it.phase.priority }) {
            list = argument.apply(finder, list)
        }

        return list
    }
}
