package io.github.takenoko4096.noctiluca.registry.block.templates

import io.github.takenoko4096.noctiluca.Noctiluca
import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.math.Position3i
import io.github.takenoko4096.noctiluca.math.Vector3d
import io.github.takenoko4096.noctiluca.portal.PortalAxis
import io.github.takenoko4096.noctiluca.portal.PortalType
import io.github.takenoko4096.noctiluca.registry.block.CustomPortalBlock
import io.github.takenoko4096.noctiluca.registry.block.ModBlockConfiguration
import io.github.takenoko4096.noctiluca.render.TexturePath
import io.github.takenoko4096.noctiluca.render.model.block.NonClientVariantMutator
import io.github.takenoko4096.noctiluca.text.ArgbColor
import io.github.takenoko4096.noctiluca.text.RgbColor
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.PushReaction

@NoctilucaDsl
class PortalBlockTemplate(callback: PortalBlockTemplate.() -> Unit) : ModBlockTemplate() {
    var color: ArgbColor = RgbColor.WHITE.withAlpha(255)

    var texturePath: TexturePath? = null

    val noctilucaCustomPortalTexturePath = Noctiluca.identifierOf("block/custom_portal")

    private var ambient: AmbientConfiguration = AmbientConfiguration {}

    fun ambient(callback: AmbientConfiguration.() -> Unit) {
        ambient = AmbientConfiguration(callback)
    }

    init {
        callback()
    }

    override fun getConfiguration(identifier: Identifier): ModBlockConfiguration.() -> Unit = {
        constructor { properties, definitions, dispatcher, function1, function2 ->
            object : CustomPortalBlock(
                properties,
                definitions,
                dispatcher,
                function1,
                function2,
                this@PortalBlockTemplate.color,
                this@PortalBlockTemplate.ambient.soundEvent,
                this@PortalBlockTemplate.ambient.sound.volume,
                this@PortalBlockTemplate.ambient.sound.pitch,
                this@PortalBlockTemplate.ambient.particle
            ) {
                override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
                    initializeProperties(builder, definitions)
                }
            }
        }

        blockProperties {
            sound = SoundType.GLASS
            destroyTime = Float.POSITIVE_INFINITY
            occlusion = false
            collision = false
            pushReaction = PushReaction.DESTROY
            isReplaceable = true
        }

        val properties = blockStates {
            enumerationProperty<PortalAxis>("axis") {
                defaultValue = PortalAxis.X
            }
        }

        events {
            onUpdate {
                val portalAxis = blockState.getValue(properties.enumeration<PortalAxis>("axis"))

                if (directionToNeighbour.axis.isHorizontal && directionToNeighbour.axis != portalAxis.toAxis()) {
                    return@onUpdate
                }

                if (neighbourState.`is`(blockState.block)) {
                    return@onUpdate
                }

                val type = PortalType.getByPortalBlock(blockState.block) ?: return@onUpdate

                if (type.portalFinder.findPortalWithAxis(level, Position3i.from(blockPos), portalAxis) { isCompletePortal() } == null) {
                    finalBlockState = Blocks.AIR.defaultBlockState()
                }
            }

            onAnimateTick {
                val customPortalBlock = blockState.block as CustomPortalBlock

                if (randomSource.nextInt(100) == 0) {

                    level.playLocalSound(
                        position.x + 0.5,
                        position.y + 0.5,
                        position.z + 0.5,
                        customPortalBlock.ambientSoundEvent,
                        SoundSource.BLOCKS,
                        AmbientConfiguration.SoundValueProvider(level, position, randomSource, customPortalBlock.ambientVolumeProvider)
                            .value.coerceIn(0.5f..2.0f),
                        AmbientConfiguration.SoundValueProvider(level, position, randomSource, customPortalBlock.ambientPitchProvider)
                            .value.coerceIn(0.5f..2.0f),
                        false
                    )
                }

                if (customPortalBlock.particleOptions != null) for (i in 0..3) {
                    var x: Double = position.x + randomSource.nextDouble()
                    val y: Double = position.y + randomSource.nextDouble()
                    var z: Double = position.z + randomSource.nextDouble()
                    var xa: Double = (randomSource.nextFloat() - 0.5) * 0.5
                    val ya: Double = (randomSource.nextFloat() - 0.5) * 0.5
                    var za: Double = (randomSource.nextFloat() - 0.5) * 0.5
                    val flip: Int = randomSource.nextInt(2) * 2 - 1
                    if (!level.getBlockState(position.toBlockPos().west()).`is`(blockState.block) && !level.getBlockState(position.toBlockPos().east()).`is`(blockState.block)) {
                        x = position.x + 0.5 + 0.25 * flip
                        xa = (randomSource.nextFloat() * 2.0f * flip).toDouble()
                    }
                    else {
                        z = position.z + 0.5 + 0.25 * flip
                        za = (randomSource.nextFloat() * 2.0f * flip).toDouble()
                    }

                    level.addParticle(customPortalBlock.particleOptions, x, y, z, xa, ya, za)
                }
            }
        }

        voxelShape {
            val x = box(Vector3d(0.0, 0.0, 6.0), Vector3d(16.0, 16.0, 10.0))
            val z = box(Vector3d(6.0, 0.0, 0.0), Vector3d(10.0, 16.0, 16.0))

            when (blockState.getValue(properties.enumeration<PortalAxis>("axis"))) {
                PortalAxis.X -> x
                PortalAxis.Z -> z
            }
        }

        rotatableInStructure {
            val axisProperty = properties.enumeration<PortalAxis>("axis")

            if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90) {
                finalBlockState = when (blockState.getValue(axisProperty)) {
                    PortalAxis.X -> blockState.setValue(axisProperty, PortalAxis.Z)
                    PortalAxis.Z -> blockState.setValue(axisProperty, PortalAxis.X)
                }
            }
        }

        model {
            val model = blockModels.fromParent(
                Noctiluca.identifierOf("block/custom_portal"),
                mapOf(
                    "portal" to (this@PortalBlockTemplate.texturePath ?: blockDefaultTexturePath),
                    "particle" to (this@PortalBlockTemplate.texturePath ?: blockDefaultTexturePath)
                )
            )

            block {
                variants(properties.enumeration<PortalAxis>("axis")) {
                    case(PortalAxis.X, model.toVariant(NonClientVariantMutator.Y_ROT_90))
                    case(PortalAxis.Z, model.toVariant())
                }
            }
        }

        color {
            default { blockState ->
                val customPortalBlock = blockState.block as CustomPortalBlock
                return@default customPortalBlock.color
            }
        }
    }

    @NoctilucaDsl
    class AmbientConfiguration internal constructor(callback: AmbientConfiguration.() -> Unit) {
        internal var soundEvent = SoundEvents.EMPTY

        internal var sound: SoundConfiguration = SoundConfiguration {}

        var particle: ParticleOptions? = null

        init {
            callback()
        }

        fun sound(callback: SoundConfiguration.() -> Unit) {
            sound = SoundConfiguration(callback)
        }

        @NoctilucaDsl
        class SoundConfiguration internal constructor(callback: SoundConfiguration.() -> Unit) {
            var soundEvent: SoundEvent = SoundEvents.EMPTY

            internal var volume: SoundValueProvider.() -> Float = { 0.5f }

            internal var pitch: SoundValueProvider.() -> Float = { randomSource.nextFloat() * 0.4f + 0.8f }

            init {
                callback()
            }

            fun volume(callback: SoundValueProvider.() -> Float) {
                volume = callback
            }

            fun pitch(callback: SoundValueProvider.() -> Float) {
                pitch = callback
            }
        }

        @NoctilucaDsl
        class SoundValueProvider internal constructor(val level: Level, val position: Position3i, val randomSource: RandomSource, callback: SoundValueProvider.() -> Float) {
            internal val value = callback()
        }
    }
}
