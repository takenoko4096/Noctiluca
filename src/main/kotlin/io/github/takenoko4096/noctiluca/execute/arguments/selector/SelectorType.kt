package io.github.takenoko4096.noctiluca.execute.arguments.selector

import io.github.takenoko4096.noctiluca.execute.NoctilucaCommandSourceStack
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType

enum class SelectorType(val aliveOnly: Boolean, vararg defaultArguments: SelectorArgument) {
    E(true, SelectorArgument.Sort(SelectorSortOrder.ARBITRARY)),
    N(true, SelectorArgument.Sort(SelectorSortOrder.NEAREST), SelectorArgument.Limit(1)),
    P(true, SelectorArgument.Sort(SelectorSortOrder.ARBITRARY), SelectorArgument.Type(EntityType.PLAYER), SelectorArgument.Limit(1)),
    R(true, SelectorArgument.Sort(SelectorSortOrder.RANDOM), SelectorArgument.Type(EntityType.PLAYER), SelectorArgument.Limit(1)),
    A(false, SelectorArgument.Sort(SelectorSortOrder.ARBITRARY), SelectorArgument.Type(EntityType.PLAYER)),
    S(false, SelectorArgument.Sort(SelectorSortOrder.ARBITRARY), object : SelectorArgument.ModifiableSelectorArgument("self") {
        override fun modify(stack: NoctilucaCommandSourceStack, entities: List<Entity>): List<Entity> {
            return stack.getExecutorOrNull()?.let { listOf(it) } ?: listOf()
        }
    });

    val defaultArguments: SelectorArguments = SelectorArguments(defaultArguments.toSet())

    fun getCandidates(stack: NoctilucaCommandSourceStack): List<Entity> {
        val entities = stack.sender.getServer().allLevels.flatMap { it.allEntities }.filter { it.type != EntityType.PLAYER }
        val players = stack.sender.getServer().playerList.players.filter {
            if (aliveOnly) it.isAlive
            else true
        }
        return (entities + players).sortedBy { it.id }
    }
}
