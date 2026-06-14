package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.portal.PortalIgnitionSource.Companion.block
import io.github.takenoko4096.noctiluca.portal.PortalType.Companion.getIgnitablePortal
import io.github.takenoko4096.noctiluca.registry.block.templates.ModBlockTemplate.AmbientConfiguration
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.sounds.SoundEvent
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.block.TntBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import kotlin.math.max
import kotlin.math.min

abstract class CustomFireBlock(
    blockBehaviourProperties: BlockBehaviour.Properties,
    blockStateProperties: Set<BlockStatesConfiguration.PropertyDefinition<*>>,
    eventDispatcher: BlockEventsConfiguration.BlockEventDispatcher,
    voxelShapeBuilder: (BlockState, BlockGetter, BlockPos, CollisionContext) -> VoxelShape,
    val ambientSoundEvent: SoundEvent,
    val ambientVolumeProvider: AmbientConfiguration.SoundValueProvider.() -> Float,
    val ambientPitchProvider: AmbientConfiguration.SoundValueProvider.() -> Float,
    val particleOptions: ParticleOptions?
) : CustomBlock(blockBehaviourProperties, blockStateProperties, eventDispatcher, voxelShapeBuilder, null) {
    val directionalProperties = mapOf(
        Direction.SOUTH to getDirectionalProperty("south"),
        Direction.NORTH to getDirectionalProperty("north"),
        Direction.EAST to getDirectionalProperty("east"),
        Direction.WEST to getDirectionalProperty("west"),
        Direction.UP to getDirectionalProperty("up")
    )

    fun getDirectionalProperty(name: String): BooleanProperty {
        return stateDefinition.getProperty(name)?.let { it as? BooleanProperty } ?: throw IllegalArgumentException()
    }

    fun getAgeProperty(): IntegerProperty {
        return stateDefinition.getProperty("age")?.let { it as? IntegerProperty } ?: throw IllegalArgumentException()
    }

    private val igniteOdds: Object2IntMap<Block> = Object2IntOpenHashMap()

    private val burnOdds: Object2IntMap<Block> = Object2IntOpenHashMap()

    public override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        val below = pos.below()
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP) || isValidFireLocation(level, pos)
    }

    override fun spawnDestroyParticles(level: Level, player: Player, pos: BlockPos, state: BlockState) {}

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        if (!level.isClientSide) {
            level.levelEvent(null, LevelEvent.SOUND_EXTINGUISH_FIRE, pos, 0)
        }
        return super.playerWillDestroy(level, pos, state, player)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, movedByPiston: Boolean) {
        run {
            if (level.isClientSide) return@run
            val portal = getIgnitablePortal(level, pos) ?: return@run
            if (!state.canSurvive(level, pos)) {
                level.removeBlock(pos, false)
            }
            portal.ignite(level, block(state.block))
            return
        }

        super.onPlace(state, level, pos, oldState, movedByPiston)
        level.scheduleTick(pos, this, 30 + level.random.nextInt(10))
    }

    internal fun isValidFireLocation(level: BlockGetter, pos: BlockPos): Boolean {
        for (direction in Direction.entries) {
            if (!canBurn(level.getBlockState(pos.relative(direction)))) continue
            return true
        }
        return false
    }

    internal fun canBurn(blockState: BlockState): Boolean {
        return getIgniteOdds(blockState) > 0
    }

    private fun getIgniteOdds(state: BlockState): Int {
        if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
            return 0
        }

        return igniteOdds.getInt(state.block)
    }

    internal fun getIgniteOdds(level: LevelReader, pos: BlockPos): Int {
        if (!level.isEmptyBlock(pos)) {
            return 0
        }
        var odds = 0
        for (direction in Direction.entries) {
            val blockState = level.getBlockState(pos.relative(direction))
            odds = max(getIgniteOdds(blockState), odds)
        }
        return odds
    }

    private fun getBurnOdds(state: BlockState): Int {
        if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
            return 0
        }

        return burnOdds.getInt(state.block)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return getBlockStateAt(context.level, context.clickedPos)
    }

    internal fun getBlockStateAt(level: BlockGetter, pos: BlockPos): BlockState {
        val below = pos.below()
        val belowState = level.getBlockState(below)
        if (canBurn(belowState) || belowState.isFaceSturdy(level, below, Direction.UP)) {
            return defaultBlockState()
        }

        var result = defaultBlockState()
        for (direction in Direction.entries) {
            val property = directionalProperties[direction] ?: continue
            result = result.setValue(property, canBurn(level.getBlockState(pos.relative(direction))))
        }
        return result
    }

    internal fun getStateWithAge(level: LevelReader, pos: BlockPos, age: Int): BlockState {
        val state = getBlockStateAt(level, pos)
        state.setValue(getAgeProperty(), age)
        return state
    }

    internal fun isNearRain(level: Level, testPos: BlockPos): Boolean {
        return level.isRainingAt(testPos)
            || level.isRainingAt(testPos.west())
            || level.isRainingAt(testPos.east())
            || level.isRainingAt(testPos.north())
            || level.isRainingAt(testPos.south())
    }

    internal fun checkBurnOut(level: Level, pos: BlockPos, chance: Int, random: RandomSource, age: Int) {
        val odds = getBurnOdds(level.getBlockState(pos))
        if (random.nextInt(chance) < odds) {
            val oldState = level.getBlockState(pos)
            if (random.nextInt(age + 10) < 5 && !level.isRainingAt(pos)) {
                val newAge = min(age + random.nextInt(5) / 4, 15)
                level.setBlock(pos, getStateWithAge(level, pos, newAge), 3)
            } else {
                level.removeBlock(pos, false)
            }
            val block = oldState.block
            if (block is TntBlock) {
                TntBlock.prime(level, pos)
            }
        }
    }

    fun setFlammable(block: Block, igniteOdds: Int, burnOdds: Int) {
        this.igniteOdds[block] = igniteOdds
        this.burnOdds[block] = burnOdds
    }
}
