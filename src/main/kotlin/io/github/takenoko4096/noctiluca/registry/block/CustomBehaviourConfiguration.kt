package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition

@NoctilucaDsl
class CustomBehaviourConfiguration {
    internal var propertyDefinitions: Set<BlockStatesConfiguration.PropertyDefinition<*>> = setOf()

    private var eventDispatcher: BlockEventsConfiguration.BlockEventDispatcher = BlockEventsConfiguration.BlockEventDispatcher(setOf())

    fun blockStates(callback: BlockStatesConfiguration.() -> Unit): Properties {
        val bsc = BlockStatesConfiguration()
        bsc.callback()
        propertyDefinitions = bsc.build()
        return Properties(propertyDefinitions.toSet())
    }

    fun events(callback: BlockEventsConfiguration.() -> Unit) {
        val bec = BlockEventsConfiguration()
        bec.callback()
        eventDispatcher = bec.build()
    }

    internal fun build(): (BlockBehaviour.Properties) -> CustomBlock {
        val defs = propertyDefinitions.toSet()
        val evs = eventDispatcher

        return {
            object : CustomBlock(it, defs, evs) {
                override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
                    for (definition in defs) {
                        builder.add(definition.property)
                    }
                }
            }
        }
    }
}
