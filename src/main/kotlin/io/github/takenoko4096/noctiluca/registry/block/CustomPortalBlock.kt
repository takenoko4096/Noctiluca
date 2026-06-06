package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.Noctiluca
import io.github.takenoko4096.noctiluca.math.toPosition3i
import io.github.takenoko4096.noctiluca.portal.PortalAxis
import io.github.takenoko4096.noctiluca.portal.PortalType
import io.github.takenoko4096.noctiluca.registry.block.templates.PortalBlockTemplate
import io.github.takenoko4096.noctiluca.text.ArgbColor
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Portal
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.portal.TeleportTransition
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
    val ambientVolumeProvider: PortalBlockTemplate.AmbientConfiguration.SoundValueProvider.() -> Float,
    val ambientPitchProvider: PortalBlockTemplate.AmbientConfiguration.SoundValueProvider.() -> Float,
    val particleOptions: ParticleOptions?
) : CustomBlock(behaviourProperties, blockStateProperties, blockEventDispatcher, voxelShapeProvider, rotator), Portal {
    fun getPortalAxisProperty(): Property<PortalAxis> {
        return stateDefinition.getProperty("axis") as Property<PortalAxis>
    }

    override fun getPortalDestination(currentLevel: ServerLevel, entity: Entity, portalEntryPos: BlockPos): TeleportTransition? {
        Noctiluca.logger.info("called CustomPortalBlock::getPortalDestination()")

        val blockState = currentLevel.getBlockState(portalEntryPos)
        val type = PortalType.getByPortalBlock(blockState.block) ?: return null
        val axisProperty = (blockState.block as CustomPortalBlock).getPortalAxisProperty()
        val axis = blockState.getValue(axisProperty)

        return type.portalFinder.findPortalWithAxis(currentLevel, portalEntryPos.toPosition3i(), axis) { isCompletePortal() }
            ?.getTeleportTransition(currentLevel)
    }
}
