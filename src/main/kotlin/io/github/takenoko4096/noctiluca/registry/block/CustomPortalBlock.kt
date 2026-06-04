package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.portal.PortalAxis
import io.github.takenoko4096.noctiluca.text.ArgbColor
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

abstract class CustomPortalBlock(
    behaviourProperties: BlockBehaviour.Properties,
    blockStateProperties: Set<BlockStatesConfiguration.PropertyDefinition<*>>,
    blockEventDispatcher: BlockEventsConfiguration.BlockEventDispatcher,
    voxelShapeProvider: ((BlockState, BlockGetter, BlockPos, CollisionContext) -> VoxelShape)?,
    rotator: ((BlockState, Rotation) -> BlockState)?,
    val color: ArgbColor,
    val ambientSoundEvent: SoundEvent,
    val ambientBasePitch: Float,
    val particleOptions: ParticleOptions
) : CustomBlock(behaviourProperties, blockStateProperties, blockEventDispatcher, voxelShapeProvider, rotator) {
    fun getPortalAxisProperty(): Property<PortalAxis> {
        return stateDefinition.getProperty("axis") as Property<PortalAxis>
    }
}
