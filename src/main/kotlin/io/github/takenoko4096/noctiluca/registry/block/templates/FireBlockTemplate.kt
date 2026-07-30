package io.github.takenoko4096.noctiluca.registry.block.templates

import io.github.takenoko4096.noctiluca.Noctiluca
import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.registry.block.BlockEventsConfiguration
import io.github.takenoko4096.noctiluca.registry.block.CustomFireBlock
import io.github.takenoko4096.noctiluca.registry.block.ModBlockConfiguration
import io.github.takenoko4096.noctiluca.render.TexturePath
import io.github.takenoko4096.noctiluca.render.model.NonClientModel
import io.github.takenoko4096.noctiluca.render.model.block.BlockModelProvider
import io.github.takenoko4096.noctiluca.render.model.block.NonClientVariantMutator
import io.github.takenoko4096.noctiluca.text.ArgbColor
import io.github.takenoko4096.noctiluca.text.RgbColor
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundSource
import net.minecraft.world.attribute.EnvironmentAttributes
import net.minecraft.world.entity.InsideBlockEffectType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.ColorCollection
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.shapes.Shapes
import kotlin.math.min

@NoctilucaDsl
class FireBlockTemplate(callback: FireBlockTemplate.() -> Unit) : ModBlockTemplate<CustomFireBlock>() {
    var texturePath0: TexturePath? = null
    var texturePath1: TexturePath? = null

    var damage: Float = 1.0f

    var color: ArgbColor = RgbColor.WHITE.withAlpha(255)

    private var ambient: AmbientConfiguration = AmbientConfiguration {}

    private var flammability = FlammabilityConfiguration {}

    fun ambient(callback: AmbientConfiguration.() -> Unit) {
        ambient = AmbientConfiguration(callback)
    }

    fun flammability(callback: FlammabilityConfiguration.() -> Unit) {
        flammability = FlammabilityConfiguration(callback)
    }

    val customFireTexturePath0 = Noctiluca.texturePathOf("block/custom_fire_0")
    val customFireTexturePath1 = Noctiluca.texturePathOf("block/custom_fire_1")

    init {
        callback()
    }

    private fun getTexturePath(defaultTexturePath: TexturePath, isZero: Boolean): TexturePath {
        return if (isZero) (texturePath0 ?: (defaultTexturePath underscore 0.toString()))
        else (texturePath1 ?: (defaultTexturePath underscore 1.toString()))
    }

    private fun getSuffix(location: String, isZero: Boolean): String {
        return location + '_' + if (isZero) 0.toString() else 1.toString()
    }

    private fun model(location: String, models: BlockModelProvider, defaultTexturePath: TexturePath, isZero: Boolean): NonClientModel {
        return models.fromParent(
            Noctiluca.identifierOf("block/custom_fire/$location"),
            mapOf(
                "fire" to getTexturePath(defaultTexturePath, isZero)
            ),
            options = { suffix = this@FireBlockTemplate.getSuffix(location, isZero) }
        )
    }

    private fun floorModel(models: BlockModelProvider, defaultTexturePath: TexturePath, isZero: Boolean): NonClientModel {
        return model("floor", models, defaultTexturePath,  isZero)
    }

    private fun sideModel(models: BlockModelProvider, defaultTexturePath: TexturePath, isZero: Boolean): NonClientModel {
        return model("side", models, defaultTexturePath, isZero)
    }

    private fun sideAltModel(models: BlockModelProvider, defaultTexturePath: TexturePath, isZero: Boolean): NonClientModel {
        return model("side_alt", models, defaultTexturePath, isZero)
    }

    private fun upModel(models: BlockModelProvider, defaultTexturePath: TexturePath, isZero: Boolean): NonClientModel {
        return model("up", models, defaultTexturePath, isZero)
    }

    private fun upAltModel(models: BlockModelProvider, defaultTexturePath: TexturePath, isZero: Boolean): NonClientModel {
        return model("up_alt", models, defaultTexturePath, isZero)
    }

    private fun onAnimateTick(event: BlockEventsConfiguration.AnimateTickEvent) {
        val fireBlock = event.blockState.block as CustomFireBlock

        val volume = AmbientConfiguration.SoundValueProvider(event.level, event.position, event.randomSource, fireBlock.ambientVolumeProvider).value
        val pitch = AmbientConfiguration.SoundValueProvider(event.level, event.position, event.randomSource, fireBlock.ambientPitchProvider).value

        run block@ {
            var zz: Double
            var yy: Double
            var xx: Double
            var i: Int
            run {
                val below: BlockPos
                val belowState: BlockState
                if (event.randomSource.nextInt(24) == 0) {
                    event.level.playLocalSound(
                        event.position.x.toDouble() + 0.5,
                        event.position.y.toDouble() + 0.5,
                        event.position.z.toDouble() + 0.5,
                        fireBlock.ambientSoundEvent,
                        SoundSource.BLOCKS,
                        volume,
                        pitch,
                        false
                    )
                }

                if (fireBlock.particleOptions == null) return@block

                if (!fireBlock.canBurn(event.level.getBlockState(event.position.toBlockPos().below().also { below = it }).also { belowState = it }) && !belowState.isFaceSturdy(event.level, below, Direction.UP)) return@run
                for (j in 0..2) {
                    val xx2: Double = event.position.x.toDouble() + event.randomSource.nextDouble()
                    val yy2: Double = event.position.y.toDouble() + event.randomSource.nextDouble() * 0.5 + 0.5
                    val zz2: Double = event.position.z.toDouble() + event.randomSource.nextDouble()
                    event.level.addParticle(fireBlock.particleOptions, xx2, yy2, zz2, 0.0, 0.0, 0.0)
                }
                return@block
            }

            if (fireBlock.particleOptions == null) return@block

            if (fireBlock.canBurn(event.level.getBlockState(event.position.toBlockPos().west()))) {
                i = 0
                while (i < 2) {
                    xx = event.position.x.toDouble() + event.randomSource.nextDouble() * 0.1
                    yy = event.position.y.toDouble() + event.randomSource.nextDouble()
                    zz = event.position.z.toDouble() + event.randomSource.nextDouble()
                    event.level.addParticle(fireBlock.particleOptions, xx, yy, zz, 0.0, 0.0, 0.0)
                    ++i
                }
            }
            if (fireBlock.canBurn(event.level.getBlockState(event.position.toBlockPos().east()))) {
                i = 0
                while (i < 2) {
                    xx = (event.position.x + 1).toDouble() - event.randomSource.nextDouble() * 0.1
                    yy = event.position.y.toDouble() + event.randomSource.nextDouble()
                    zz = event.position.z.toDouble() + event.randomSource.nextDouble()
                    event.level.addParticle(fireBlock.particleOptions, xx, yy, zz, 0.0, 0.0, 0.0)
                    ++i
                }
            }
            if (fireBlock.canBurn(event.level.getBlockState(event.position.toBlockPos().north()))) {
                i = 0
                while (i < 2) {
                    xx = event.position.x.toDouble() + event.randomSource.nextDouble()
                    yy = event.position.y.toDouble() + event.randomSource.nextDouble()
                    zz = event.position.z.toDouble() + event.randomSource.nextDouble() * 0.1
                    event.level.addParticle(fireBlock.particleOptions, xx, yy, zz, 0.0, 0.0, 0.0)
                    ++i
                }
            }
            if (fireBlock.canBurn(event.level.getBlockState(event.position.toBlockPos().south()))) {
                i = 0
                while (i < 2) {
                    xx = event.position.x.toDouble() + event.randomSource.nextDouble()
                    yy = event.position.y.toDouble() + event.randomSource.nextDouble()
                    zz = (event.position.z + 1).toDouble() - event.randomSource.nextDouble() * 0.1
                    event.level.addParticle(fireBlock.particleOptions, xx, yy, zz, 0.0, 0.0, 0.0)
                    ++i
                }
            }
            if (!fireBlock.canBurn(event.level.getBlockState(event.position.toBlockPos().above()))) return@block
            i = 0
            while (i < 2) {
                xx = event.position.x.toDouble() + event.randomSource.nextDouble()
                yy = (event.position.y + 1).toDouble() - event.randomSource.nextDouble() * 0.1
                zz = event.position.z.toDouble() + event.randomSource.nextDouble()
                event.level.addParticle(fireBlock.particleOptions, xx, yy, zz, 0.0, 0.0, 0.0)
                ++i
            }
        }
    }

    private fun tick(event: BlockEventsConfiguration.TickEvent) {
        val level = event.level
        val blockState = event.blockState
        val position = event.position
        val randomSource = event.randomSource

        val fireBlock = blockState.block as CustomFireBlock
        level.scheduleTick(position.toBlockPos(), blockState.block, 30 + randomSource.nextInt(10))
        if (!level.canSpreadFireAround(position.toBlockPos())) {
            return
        }
        if (!blockState.canSurvive(level, position.toBlockPos())) {
            level.removeBlock(position.toBlockPos(), false)
        }
        val belowState = level.getBlockState(position.toBlockPos().below())
        val infiniBurn = belowState.`is`(level.dimensionType().infiniburn())
        val age = blockState.getValue(fireBlock.getAgeProperty())
        if (!infiniBurn && level.isRaining && fireBlock.isNearRain(level, position.toBlockPos()) && randomSource.nextFloat() < 0.2f + age * 0.03f) {
            level.removeBlock(position.toBlockPos(), false)
            return
        }
        val newAge = min(15, age + randomSource.nextInt(3) / 2)
        if (age != newAge) {
            level.setBlock(
                position.toBlockPos(),
                blockState.setValue(fireBlock.getAgeProperty(), newAge),
                260
            )
        }
        if (!infiniBurn) {
            if (!fireBlock.isValidFireLocation(level, position.toBlockPos())) {
                val below = position.toBlockPos().below()
                if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP) || age > 3) {
                    level.removeBlock(position.toBlockPos(), false)
                }
                return
            }
            if (age == 15 && randomSource.nextInt(4) == 0 && !fireBlock.canBurn(level.getBlockState(position.toBlockPos().below()))) {
                level.removeBlock(position.toBlockPos(), false)
                return
            }
        }
        val increasedBurnout: Boolean = level.environmentAttributes()
            .getValue(EnvironmentAttributes.INCREASED_FIRE_BURNOUT, position.toBlockPos())
        val extra = if (increasedBurnout) -50 else 0
        fireBlock.checkBurnOut(level, position.toBlockPos().east(), 300 + extra, randomSource, age)
        fireBlock.checkBurnOut(level, position.toBlockPos().west(), 300 + extra, randomSource, age)
        fireBlock.checkBurnOut(level, position.toBlockPos().below(), 250 + extra, randomSource, age)
        fireBlock.checkBurnOut(level, position.toBlockPos().above(), 250 + extra, randomSource, age)
        fireBlock.checkBurnOut(level, position.toBlockPos().north(), 300 + extra, randomSource, age)
        fireBlock.checkBurnOut(level, position.toBlockPos().south(), 300 + extra, randomSource, age)
        val testPos = BlockPos.MutableBlockPos()
        for (xx in -1..1) {
            for (zz in -1..1) {
                for (yy in -1..4) {
                    if (xx == 0 && yy == 0 && zz == 0) continue
                    var rate = 100
                    if (yy > 1) {
                        rate += (yy - 1) * 100
                    }
                    testPos.setWithOffset(position.toBlockPos(), xx, yy, zz)
                    val igniteOdds = fireBlock.getIgniteOdds(level, testPos)
                    if (igniteOdds <= 0) continue
                    var odds = (igniteOdds + 40 + level.difficulty.id * 7) / (age + 30)
                    if (increasedBurnout) {
                        odds /= 2
                    }
                    if (odds <= 0 || randomSource.nextInt(rate) > odds || level.isRaining && fireBlock.isNearRain(level, testPos)) continue
                    val spreadAge = min(15, age + randomSource.nextInt(5) / 4)
                    level.setBlock(testPos, fireBlock.getStateWithAge(level, testPos, spreadAge), 3)
                }
            }
        }
    }

    override fun getConfigurator(identifier: Identifier): ModBlockConfiguration.() -> Unit = {
        blockProperties {
            this@FireBlockTemplate.run {
                this@blockProperties.setMapColor()
            }
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

        val color = this@FireBlockTemplate.color
        color {
            default { color }
        }

        model {
            val floor0 = this@FireBlockTemplate.floorModel(blockModels, blockDefaultTexturePath, true).toVariant()
            val floor1 = this@FireBlockTemplate.floorModel(blockModels, blockDefaultTexturePath, false).toVariant()
            val side0 = this@FireBlockTemplate.sideModel(blockModels, blockDefaultTexturePath, true).toVariant()
            val side1 = this@FireBlockTemplate.sideModel(blockModels, blockDefaultTexturePath, false).toVariant()
            val sideAlt0 = this@FireBlockTemplate.sideAltModel(blockModels, blockDefaultTexturePath, true).toVariant()
            val sideAlt1 = this@FireBlockTemplate.sideAltModel(blockModels, blockDefaultTexturePath, false).toVariant()
            val up0 = this@FireBlockTemplate.upModel(blockModels, blockDefaultTexturePath, true).toVariant()
            val up1 = this@FireBlockTemplate.upModel(blockModels, blockDefaultTexturePath, false).toVariant()
            val upAlt0 = this@FireBlockTemplate.upAltModel(blockModels, blockDefaultTexturePath, true).toVariant()
            val upAlt1 = this@FireBlockTemplate.upAltModel(blockModels, blockDefaultTexturePath, false).toVariant()

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

            val fireBlock = blockState.block as CustomFireBlock

            var shape = Shapes.empty()
            for ((direction, property) in fireBlock.directionalProperties) {
                if (!blockState.getValue(property)) continue
                shape = Shapes.or(shape, shapes[direction]!!)
            }

            if (shape.isEmpty) xzSizedBox(16.0, 0.0..1.0, 16.0) else shape
        }

        events {
            onUpdate {
                val fireBlock = blockState.block as CustomFireBlock

                finalBlockState = if (fireBlock.canSurvive(blockState, level, blockPos)) {
                    fireBlock.getStateWithAge(level, blockPos, blockState.getValue(fireBlock.getAgeProperty()))
                }
                else {
                    Blocks.AIR.defaultBlockState()
                }
            }

            onEntityInsideBlock {
                insideBlockEffectApplier.apply(InsideBlockEffectType.CLEAR_FREEZE)
                insideBlockEffectApplier.apply(InsideBlockEffectType.FIRE_IGNITE)
                insideBlockEffectApplier.runAfter(InsideBlockEffectType.FIRE_IGNITE) {
                    it.hurt(it.level().damageSources().inFire(), this@FireBlockTemplate.damage)
                }
            }

            onAnimateTick(this@FireBlockTemplate::onAnimateTick)

            tick(this@FireBlockTemplate::tick)
        }

        constructor { properties, definitions, dispatcher, function1, _ ->
            object : CustomFireBlock(
                properties,
                definitions,
                dispatcher,
                function1!!,
                this@FireBlockTemplate.ambient.sound.soundEvent,
                this@FireBlockTemplate.ambient.sound.volume ?: { 1.0f + randomSource.nextFloat() },
                this@FireBlockTemplate.ambient.sound.pitch ?: { randomSource.nextFloat() * 0.7f + 0.3f },
                this@FireBlockTemplate.ambient.particle
            ) {
                override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
                    initializeProperties(builder, definitions)
                }
            }.apply {
                this@FireBlockTemplate.flammability.apply(this)
            }
        }
    }

    @NoctilucaDsl
    class FlammabilityConfiguration internal constructor(callback: FlammabilityConfiguration.() -> Unit) {
        private val odds = mutableMapOf<Block, Pair<Int, Int>>()

        init {
            callback()
        }

        fun set(block: Block, igniteOdds: Int, burnOdds: Int) {
            odds[block] = igniteOdds to burnOdds
        }

        fun set(blocks: ColorCollection<Block>, igniteOdds: Int, burnOdds: Int) {
            blocks.forEach {
                odds[it] = igniteOdds to burnOdds
            }
        }

        fun setByUsingVanilla() {
            set(Blocks.OAK_PLANKS, 5, 20)
            set(Blocks.SPRUCE_PLANKS, 5, 20)
            set(Blocks.BIRCH_PLANKS, 5, 20)
            set(Blocks.JUNGLE_PLANKS, 5, 20)
            set(Blocks.ACACIA_PLANKS, 5, 20)
            set(Blocks.CHERRY_PLANKS, 5, 20)
            set(Blocks.DARK_OAK_PLANKS, 5, 20)
            set(Blocks.PALE_OAK_PLANKS, 5, 20)
            set(Blocks.MANGROVE_PLANKS, 5, 20)
            set(Blocks.BAMBOO_PLANKS, 5, 20)
            set(Blocks.BAMBOO_MOSAIC, 5, 20)
            set(Blocks.OAK_SLAB, 5, 20)
            set(Blocks.SPRUCE_SLAB, 5, 20)
            set(Blocks.BIRCH_SLAB, 5, 20)
            set(Blocks.JUNGLE_SLAB, 5, 20)
            set(Blocks.ACACIA_SLAB, 5, 20)
            set(Blocks.CHERRY_SLAB, 5, 20)
            set(Blocks.DARK_OAK_SLAB, 5, 20)
            set(Blocks.PALE_OAK_SLAB, 5, 20)
            set(Blocks.MANGROVE_SLAB, 5, 20)
            set(Blocks.BAMBOO_SLAB, 5, 20)
            set(Blocks.BAMBOO_MOSAIC_SLAB, 5, 20)
            set(Blocks.OAK_FENCE_GATE, 5, 20)
            set(Blocks.SPRUCE_FENCE_GATE, 5, 20)
            set(Blocks.BIRCH_FENCE_GATE, 5, 20)
            set(Blocks.JUNGLE_FENCE_GATE, 5, 20)
            set(Blocks.ACACIA_FENCE_GATE, 5, 20)
            set(Blocks.CHERRY_FENCE_GATE, 5, 20)
            set(Blocks.DARK_OAK_FENCE_GATE, 5, 20)
            set(Blocks.PALE_OAK_FENCE_GATE, 5, 20)
            set(Blocks.MANGROVE_FENCE_GATE, 5, 20)
            set(Blocks.BAMBOO_FENCE_GATE, 5, 20)
            set(Blocks.OAK_FENCE, 5, 20)
            set(Blocks.SPRUCE_FENCE, 5, 20)
            set(Blocks.BIRCH_FENCE, 5, 20)
            set(Blocks.JUNGLE_FENCE, 5, 20)
            set(Blocks.ACACIA_FENCE, 5, 20)
            set(Blocks.CHERRY_FENCE, 5, 20)
            set(Blocks.DARK_OAK_FENCE, 5, 20)
            set(Blocks.PALE_OAK_FENCE, 5, 20)
            set(Blocks.MANGROVE_FENCE, 5, 20)
            set(Blocks.BAMBOO_FENCE, 5, 20)
            set(Blocks.OAK_STAIRS, 5, 20)
            set(Blocks.BIRCH_STAIRS, 5, 20)
            set(Blocks.SPRUCE_STAIRS, 5, 20)
            set(Blocks.JUNGLE_STAIRS, 5, 20)
            set(Blocks.ACACIA_STAIRS, 5, 20)
            set(Blocks.CHERRY_STAIRS, 5, 20)
            set(Blocks.DARK_OAK_STAIRS, 5, 20)
            set(Blocks.PALE_OAK_STAIRS, 5, 20)
            set(Blocks.MANGROVE_STAIRS, 5, 20)
            set(Blocks.BAMBOO_STAIRS, 5, 20)
            set(Blocks.BAMBOO_MOSAIC_STAIRS, 5, 20)
            set(Blocks.OAK_LOG, 5, 5)
            set(Blocks.SPRUCE_LOG, 5, 5)
            set(Blocks.BIRCH_LOG, 5, 5)
            set(Blocks.JUNGLE_LOG, 5, 5)
            set(Blocks.ACACIA_LOG, 5, 5)
            set(Blocks.CHERRY_LOG, 5, 5)
            set(Blocks.PALE_OAK_LOG, 5, 5)
            set(Blocks.DARK_OAK_LOG, 5, 5)
            set(Blocks.MANGROVE_LOG, 5, 5)
            set(Blocks.BAMBOO_BLOCK, 5, 5)
            set(Blocks.STRIPPED_OAK_LOG, 5, 5)
            set(Blocks.STRIPPED_SPRUCE_LOG, 5, 5)
            set(Blocks.STRIPPED_BIRCH_LOG, 5, 5)
            set(Blocks.STRIPPED_JUNGLE_LOG, 5, 5)
            set(Blocks.STRIPPED_ACACIA_LOG, 5, 5)
            set(Blocks.STRIPPED_CHERRY_LOG, 5, 5)
            set(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5)
            set(Blocks.STRIPPED_PALE_OAK_LOG, 5, 5)
            set(Blocks.STRIPPED_MANGROVE_LOG, 5, 5)
            set(Blocks.STRIPPED_BAMBOO_BLOCK, 5, 5)
            set(Blocks.STRIPPED_OAK_WOOD, 5, 5)
            set(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5)
            set(Blocks.STRIPPED_BIRCH_WOOD, 5, 5)
            set(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5)
            set(Blocks.STRIPPED_ACACIA_WOOD, 5, 5)
            set(Blocks.STRIPPED_CHERRY_WOOD, 5, 5)
            set(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5)
            set(Blocks.STRIPPED_PALE_OAK_WOOD, 5, 5)
            set(Blocks.STRIPPED_MANGROVE_WOOD, 5, 5)
            set(Blocks.OAK_WOOD, 5, 5)
            set(Blocks.SPRUCE_WOOD, 5, 5)
            set(Blocks.BIRCH_WOOD, 5, 5)
            set(Blocks.JUNGLE_WOOD, 5, 5)
            set(Blocks.ACACIA_WOOD, 5, 5)
            set(Blocks.CHERRY_WOOD, 5, 5)
            set(Blocks.PALE_OAK_WOOD, 5, 5)
            set(Blocks.DARK_OAK_WOOD, 5, 5)
            set(Blocks.MANGROVE_WOOD, 5, 5)
            set(Blocks.MANGROVE_ROOTS, 5, 20)
            set(Blocks.OAK_LEAVES, 30, 60)
            set(Blocks.SPRUCE_LEAVES, 30, 60)
            set(Blocks.BIRCH_LEAVES, 30, 60)
            set(Blocks.JUNGLE_LEAVES, 30, 60)
            set(Blocks.ACACIA_LEAVES, 30, 60)
            set(Blocks.CHERRY_LEAVES, 30, 60)
            set(Blocks.DARK_OAK_LEAVES, 30, 60)
            set(Blocks.PALE_OAK_LEAVES, 30, 60)
            set(Blocks.MANGROVE_LEAVES, 30, 60)
            set(Blocks.BOOKSHELF, 30, 20)
            set(Blocks.TNT, 15, 100)
            set(Blocks.SHORT_GRASS, 60, 100)
            set(Blocks.FERN, 60, 100)
            set(Blocks.DEAD_BUSH, 60, 100)
            set(Blocks.SHORT_DRY_GRASS, 60, 100)
            set(Blocks.TALL_DRY_GRASS, 60, 100)
            set(Blocks.SUNFLOWER, 60, 100)
            set(Blocks.LILAC, 60, 100)
            set(Blocks.ROSE_BUSH, 60, 100)
            set(Blocks.PEONY, 60, 100)
            set(Blocks.TALL_GRASS, 60, 100)
            set(Blocks.LARGE_FERN, 60, 100)
            set(Blocks.DANDELION, 60, 100)
            set(Blocks.GOLDEN_DANDELION, 60, 100)
            set(Blocks.POPPY, 60, 100)
            set(Blocks.OPEN_EYEBLOSSOM, 60, 100)
            set(Blocks.CLOSED_EYEBLOSSOM, 60, 100)
            set(Blocks.BLUE_ORCHID, 60, 100)
            set(Blocks.ALLIUM, 60, 100)
            set(Blocks.AZURE_BLUET, 60, 100)
            set(Blocks.RED_TULIP, 60, 100)
            set(Blocks.ORANGE_TULIP, 60, 100)
            set(Blocks.WHITE_TULIP, 60, 100)
            set(Blocks.PINK_TULIP, 60, 100)
            set(Blocks.OXEYE_DAISY, 60, 100)
            set(Blocks.CORNFLOWER, 60, 100)
            set(Blocks.LILY_OF_THE_VALLEY, 60, 100)
            set(Blocks.TORCHFLOWER, 60, 100)
            set(Blocks.PITCHER_PLANT, 60, 100)
            set(Blocks.WITHER_ROSE, 60, 100)
            set(Blocks.PINK_PETALS, 60, 100)
            set(Blocks.WILDFLOWERS, 60, 100)
            set(Blocks.LEAF_LITTER, 60, 100)
            set(Blocks.CACTUS_FLOWER, 60, 100)
            set(Blocks.WOOL, 30, 60)
            set(Blocks.VINE, 15, 100)
            set(Blocks.COAL_BLOCK, 5, 5)
            set(Blocks.HAY_BLOCK, 60, 20)
            set(Blocks.TARGET, 15, 20)
            set(Blocks.CARPET, 60, 20)
            set(Blocks.PALE_MOSS_BLOCK, 5, 100)
            set(Blocks.PALE_MOSS_CARPET, 5, 100)
            set(Blocks.PALE_HANGING_MOSS, 5, 100)
            set(Blocks.DRIED_KELP_BLOCK, 30, 60)
            set(Blocks.BAMBOO, 60, 60)
            set(Blocks.SCAFFOLDING, 60, 60)
            set(Blocks.LECTERN, 30, 20)
            set(Blocks.COMPOSTER, 5, 20)
            set(Blocks.SWEET_BERRY_BUSH, 60, 100)
            set(Blocks.BEEHIVE, 5, 20)
            set(Blocks.BEE_NEST, 30, 20)
            set(Blocks.AZALEA_LEAVES, 30, 60)
            set(Blocks.FLOWERING_AZALEA_LEAVES, 30, 60)
            set(Blocks.CAVE_VINES, 15, 60)
            set(Blocks.CAVE_VINES_PLANT, 15, 60)
            set(Blocks.SPORE_BLOSSOM, 60, 100)
            set(Blocks.AZALEA, 30, 60)
            set(Blocks.FLOWERING_AZALEA, 30, 60)
            set(Blocks.BIG_DRIPLEAF, 60, 100)
            set(Blocks.BIG_DRIPLEAF_STEM, 60, 100)
            set(Blocks.SMALL_DRIPLEAF, 60, 100)
            set(Blocks.HANGING_ROOTS, 30, 60)
            set(Blocks.GLOW_LICHEN, 15, 100)
            set(Blocks.FIREFLY_BUSH, 60, 100)
            set(Blocks.BUSH, 60, 100)
            set(Blocks.ACACIA_SHELF, 30, 20)
            set(Blocks.BAMBOO_SHELF, 30, 20)
            set(Blocks.BIRCH_SHELF, 30, 20)
            set(Blocks.CHERRY_SHELF, 30, 20)
            set(Blocks.DARK_OAK_SHELF, 30, 20)
            set(Blocks.JUNGLE_SHELF, 30, 20)
            set(Blocks.MANGROVE_SHELF, 30, 20)
            set(Blocks.OAK_SHELF, 30, 20)
            set(Blocks.PALE_OAK_SHELF, 30, 20)
            set(Blocks.SPRUCE_SHELF, 30, 20)
        }

        internal fun apply(cf: CustomFireBlock) {
            for ((block, pair) in odds) {
                val (igniteOdds, burnOdds) = pair
                cf.setFlammable(block, igniteOdds, burnOdds)
            }
        }
    }
}
