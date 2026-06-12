package io.github.takenoko4096.noctiluca.registry.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

abstract class CustomFireBlock(
    blockBehaviourProperties: BlockBehaviour.Properties,
    blockStateProperties: Set<BlockStatesConfiguration.PropertyDefinition<*>>,
    eventDispatcher: BlockEventsConfiguration.BlockEventDispatcher,
    voxelShapeBuilder: (BlockState, BlockGetter, BlockPos, CollisionContext) -> VoxelShape
) : CustomBlock(blockBehaviourProperties, blockStateProperties, eventDispatcher, voxelShapeBuilder, null) {
    /*override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        val below = pos.below()
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)
            || this.isValidFireLocation(level, pos)
    }*/

    private fun isValidFireLocation(level: BlockGetter, pos: BlockPos): Boolean {
        for (direction in Direction.entries) {
            if (!this.canBurn(level.getBlockState(pos.relative(direction)))) continue
            return true
        }
        return false
    }

    private fun getIgniteOdds(state: BlockState): Int {
        if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
            return 0
        }
        return this.igniteOdds.getInt(state.block)
    }*/
}
