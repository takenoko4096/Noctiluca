package io.github.takenoko4096.noctiluca.registry.block.templates

import io.github.takenoko4096.noctiluca.Noctiluca
import io.github.takenoko4096.noctiluca.registry.block.CustomFireBlock
import io.github.takenoko4096.noctiluca.registry.block.ModBlockConfiguration
import io.github.takenoko4096.noctiluca.render.TexturePath
import io.github.takenoko4096.noctiluca.render.model.NonClientModel
import io.github.takenoko4096.noctiluca.render.model.block.BlockModelProvider
import io.github.takenoko4096.noctiluca.render.model.block.NonClientVariantMutator
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.InsideBlockEffectType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.FireBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.shapes.Shapes

class FireBlockTemplate : ModBlockTemplate() {
    var texturePath0: TexturePath? = null
    var texturePath1: TexturePath? = null

    var fireDamage: Float = 1.0f



    val customFireTexturePath0 = Noctiluca.texturePathOf("block/custom_fire_0")
    val customFireTexturePath1 = Noctiluca.texturePathOf("block/custom_fire_1")

    private fun getTexturePath(isZero: Boolean): TexturePath {
        return if (isZero) (texturePath0 ?: customFireTexturePath0) else (texturePath1 ?: customFireTexturePath1)
    }

    private fun getSuffix(isZero: Boolean): String {
        return if (isZero) 0.toString() else 1.toString()
    }

    private fun model(location: String, models: BlockModelProvider, isZero: Boolean): NonClientModel {
        return models.fromParent(
            Noctiluca.identifierOf("block/custom_fire/$location"),
            mapOf(
                "fire" to getTexturePath(isZero)
            ),
            options = { suffix = getSuffix(isZero) }
        )
    }

    private fun floorModel(models: BlockModelProvider, isZero: Boolean): NonClientModel {
        return model("floor", models, isZero)
    }

    private fun sideModel(models: BlockModelProvider, isZero: Boolean): NonClientModel {
        return model("side", models, isZero)
    }

    private fun sideAltModel(models: BlockModelProvider, isZero: Boolean): NonClientModel {
        return model("side_alt", models, isZero)
    }

    private fun upModel(models: BlockModelProvider, isZero: Boolean): NonClientModel {
        return model("top", models, isZero)
    }

    private fun upAltModel(models: BlockModelProvider, isZero: Boolean): NonClientModel {
        return model("top_alt", models, isZero)
    }

    override fun getConfigurator(identifier: Identifier): ModBlockConfiguration.() -> Unit = {
        blockProperties {
            setMapColor()
            isReplaceable = true
            collision = false
            lightLevel { 15 }
            sound = SoundType.WOOL
            pushReaction = PushReaction.DESTROY
            destroyTime = 0f
            explosionResistance = 0f
        }

        val properties = blockStates {
            booleanProperty("south") { defaultValue = false }
            booleanProperty("north") { defaultValue = false }
            booleanProperty("east") { defaultValue = false }
            booleanProperty("west") { defaultValue = false }
            booleanProperty("up") { defaultValue = false }
            integerProperty("age") { range = 0..15; defaultValue = 0 }
        }

        val south = properties.boolean("south")
        val north = properties.boolean("north")
        val east = properties.boolean("east")
        val west = properties.boolean("west")
        val up = properties.boolean("up")

        model {
            val floor0 = floorModel(blockModels, true).toVariant()
            val floor1 = floorModel(blockModels, false).toVariant()
            val side0 = sideModel(blockModels, true).toVariant()
            val side1 = sideModel(blockModels, false).toVariant()
            val sideAlt0 = sideAltModel(blockModels, true).toVariant()
            val sideAlt1 = sideAltModel(blockModels, false).toVariant()
            val up0 = upModel(blockModels, true).toVariant()
            val up1 = upModel(blockModels, false).toVariant()
            val upAlt0 = upAltModel(blockModels, true).toVariant()
            val upAlt1 = upAltModel(blockModels, false).toVariant()

            block {
                val noSides = multiPartConditions.and(
                    multiPartConditions.propertyCases(south, false),
                    multiPartConditions.propertyCases(north, false),
                    multiPartConditions.propertyCases(east, false),
                    multiPartConditions.propertyCases(west, false),
                    multiPartConditions.propertyCases(up, false)
                )

                val floorModels = multiVariant(floor0, floor1)
                val sideModels = multiVariant(side0, side1, sideAlt0, sideAlt1)
                val upModels = multiVariant(up0, up1, upAlt0, upAlt1)

                multiPart(
                    {
                        `when` { noSides }

                        apply(floorModels)
                    },
                    {
                        `when` {
                            or(
                                noSides,
                                propertyCases(north, true)
                            )
                        }

                        apply(sideModels)
                    },
                    {
                        `when` {
                            or(
                                noSides,
                                propertyCases(east, true)
                            )
                        }

                        apply(sideModels.mutatedCopy(NonClientVariantMutator.Y_ROT_90))
                    },
                    {
                        `when` {
                            or(
                                noSides,
                                propertyCases(south, true)
                            )
                        }

                        apply(sideModels.mutatedCopy(NonClientVariantMutator.Y_ROT_180))
                    },
                    {
                        `when` {
                            or(
                                noSides,
                                propertyCases(west, true)
                            )
                        }

                        apply(sideModels.mutatedCopy(NonClientVariantMutator.Y_ROT_270))
                    },
                    {
                        `when` { propertyCases(up, true) }

                        apply(upModels)
                    }
                )
            }
        }

        voxelShape {
            val shapes = Shapes.rotateAll(Block.boxZ(16.0, 0.0, 1.0))

            var shape = Shapes.empty()
            for ((direction, property) in FireBlock.PROPERTY_BY_DIRECTION) {
                if (!blockState.getValue(property)) continue
                shape = Shapes.or(shape, shapes[direction])
            }

            if (shape.isEmpty) xzSizedBox(16.0, 0.0..1.0, 16.0) else shape
        }

        events {
            onEntityInsideBlock {
                insideBlockEffectApplier.apply(InsideBlockEffectType.CLEAR_FREEZE)
                insideBlockEffectApplier.apply(InsideBlockEffectType.FIRE_IGNITE)
                insideBlockEffectApplier.runAfter(InsideBlockEffectType.FIRE_IGNITE) {
                    it.hurt(it.level().damageSources().inFire(), fireDamage)
                }
            }
        }

        constructor { properties, definitions, dispatcher, function1, _ ->
            object : CustomFireBlock(properties, definitions, dispatcher, function1!!) {
                override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
                    initializeProperties(builder, definitions)
                }
            }
        }
    }
}
