package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.Noctiluca
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.math.Position3i
import io.github.takenoko4096.noctiluca.math.Vector3d
import io.github.takenoko4096.noctiluca.portal.PortalAxis
import io.github.takenoko4096.noctiluca.portal.PortalType
import io.github.takenoko4096.noctiluca.registry.StarlightRegistry
import io.github.takenoko4096.noctiluca.render.TexturePath
import io.github.takenoko4096.noctiluca.render.model.block.NonClientVariantMutator
import io.github.takenoko4096.noctiluca.text.ArgbColor
import io.github.takenoko4096.noctiluca.text.RgbColor
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.PushReaction
import kotlin.math.min

class ModBlockRegistry(mod: NoctilucaModInitializer) : StarlightRegistry(mod) {
    private val configurations = mutableSetOf<ModBlockConfiguration>()

    internal val blocks = mutableMapOf<ResourceKey<Block>, Block>()

    private val properties = mutableMapOf<Block, Properties>()

    fun register(identifier: String, configuration: ModBlockConfiguration.() -> Unit): Block {
        val o = ModBlockConfiguration(this, identifier)
        o.configuration()
        val block = o.register()
        configurations.add(o)
        properties[block] = Properties(o.propertyDefinitions.toSet())
        blocks[o.blockResourceKey] = block
        return block
    }

    fun registerPortalBlock(identifier: String, color: ArgbColor, ambient: SoundEvent, pitch: Float, particle: ParticleOptions, texturePath: TexturePath? = null): CustomPortalBlock {
        return register(identifier) {
            constructor { properties, definitions, dispatcher, function1, function2 ->
                object : CustomPortalBlock(
                    properties,
                    definitions,
                    dispatcher,
                    function1,
                    function2,
                    color,
                    ambient,
                    pitch,
                    particle
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
                            0.5f,
                            min(2.0f, randomSource.nextFloat() * 0.4f + customPortalBlock.ambientBasePitch),
                            false
                        )
                    }

                    for (i in 0..3) {
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
                        "portal" to (texturePath ?: blockDefaultTexturePath),
                        "particle" to (texturePath ?: blockDefaultTexturePath)
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
        } as CustomPortalBlock
    }

    fun getPropertiesOf(block: Block): Properties {
        return properties[block] ?: throw IllegalArgumentException("properties not found")
    }

    fun getBlock(resourceKey: ResourceKey<Block>): Block {
        return blocks[resourceKey] ?: throw IllegalArgumentException("ブロック '${resourceKey.identifier()}' が Modブロックレジストリに見つかりませんでした: ${blocks.keys.map { it.identifier() }}")
    }

    fun getConfigurations(): Set<ModBlockConfiguration> {
        return configurations.toSet()
    }
}
