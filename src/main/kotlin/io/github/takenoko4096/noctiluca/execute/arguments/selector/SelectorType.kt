package io.github.takenoko4096.noctiluca.execute.arguments.selector

import io.github.takenoko4096.noctiluca.execute.NoctilucaCommandSourceStack
import net.minecraft.commands.arguments.selector.EntitySelector
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType

open class SelectorType(private val aliveOnly: Boolean, private val defaultArguments: SelectorArgumentSet) {
    private fun getCandidates(stack: NoctilucaCommandSourceStack): List<Entity> {
        val entities = stack.sender.getServer().allLevels.flatMap { it.allEntities }.filter { it.type != EntityType.PLAYER }
        val players = stack.sender.getServer().playerList.players.filter {
            if (aliveOnly) it.isAlive
            else true
        }

        return defaultArguments.apply(stack, entities + players)
    }

    object E : SelectorType(true, SelectorArgumentSet())
}
