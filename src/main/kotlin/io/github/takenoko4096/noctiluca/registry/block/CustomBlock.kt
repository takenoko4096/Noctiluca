package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.Noctiluca
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.DoublePlantBlock
import net.minecraft.world.level.block.NetherPortalBlock
import net.minecraft.world.level.block.ObserverBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.jetbrains.annotations.ApiStatus
import java.lang.reflect.Field
import java.util.function.BiConsumer

@ApiStatus.Experimental
open class CustomBlock internal constructor(
    properties: BlockBehaviour.Properties,
    propertyDefinitions: Set<BlockStatesConfiguration.PropertyDefinition<*>>,
    private val eventDispatcher: BlockEventsConfiguration.BlockEventDispatcher
) : Block(properties) {
    /**
     * 呼び出し順は
     * Block(properties)
     *      の中の createBlockStateDefinition()
     *          の中の this.propertyDefinitions
     * ↓
     * private val propertyDefinitions (プロパティ初期化)
     *
     * propertyDefinitions の初期化や init {} の前に呼ばれるのが createBlockStateDefinition()
     */

    init {
        var defaultState = defaultBlockState()

        for (definition in propertyDefinitions) {
            defaultState = definition.setDefaultValueTo(defaultState)
        }

        registerDefaultState(defaultState)
    }

    override fun stepOn(level: Level, blockPos: BlockPos, blockState: BlockState, entity: Entity) {
        val event = BlockEventsConfiguration.StepOnEvent(level, blockState, blockPos, entity)
        eventDispatcher.dispatch(BlockEventsConfiguration.StepOnEvent::class, event)
    }

    override fun fallOn(level: Level, blockState: BlockState, blockPos: BlockPos, entity: Entity, d: Double) {
        val event = BlockEventsConfiguration.FallOnEvent(level, blockState, blockPos, entity, d)
        eventDispatcher.dispatch(BlockEventsConfiguration.FallOnEvent::class, event)

        if (event.causeDamage) {
            super.fallOn(level, blockState, blockPos, entity, event.distance)
        }
    }

    override fun onExplosionHit(blockState: BlockState, serverLevel: ServerLevel, blockPos: BlockPos, explosion: Explosion, biConsumer: BiConsumer<ItemStack, BlockPos>) {
        val event = BlockEventsConfiguration.ExplosionHitEvent(serverLevel, blockState, blockPos, explosion, false) {
            biConsumer.accept(it.itemStack, it.blockPos)
        }
        eventDispatcher.dispatch(BlockEventsConfiguration.ExplosionHitEvent::class, event)

        if (!event.ignore) {
            super.onExplosionHit(blockState, serverLevel, blockPos, explosion) { itemStack, blockPos ->
                event.dropHandle(BlockEventsConfiguration.ExplosionHitEvent.ExplosionDrop(itemStack, blockPos))
            }
        }
    }

    override fun updateShape(state: BlockState, level: LevelReader, ticks: ScheduledTickAccess, pos: BlockPos, directionToNeighbour: Direction, neighbourPos: BlockPos, neighbourState: BlockState, random: RandomSource): BlockState {
        if (level !is Level) {
            Noctiluca.logger.info("UPDATE SHAPE FAILURE")
            return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
        }

        val event = BlockEventsConfiguration.UpdateEvent(level, state, pos, directionToNeighbour, neighbourPos, neighbourState, ticks, random)
        eventDispatcher.dispatch(BlockEventsConfiguration.UpdateEvent::class, event)

        return event.finalBlockState ?: super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random)
    }

    override fun useItemOn(itemStack: ItemStack, blockState: BlockState, level: Level, blockPos: BlockPos, player: Player, interactionHand: InteractionHand, blockHitResult: BlockHitResult): InteractionResult {
        val event = BlockEventsConfiguration.InteractEvent(level, blockState, blockPos, player, blockHitResult, interactionHand, itemStack, InteractionResult.SUCCESS)

        eventDispatcher.dispatch(BlockEventsConfiguration.InteractEvent::class, event)

        return event.interactionResult
    }

    override fun useWithoutItem(blockState: BlockState, level: Level, blockPos: BlockPos, player: Player, blockHitResult: BlockHitResult): InteractionResult {
        val event = BlockEventsConfiguration.InteractEvent(level, blockState, blockPos, player, blockHitResult, InteractionHand.MAIN_HAND, null, InteractionResult.SUCCESS)

        eventDispatcher.dispatch(BlockEventsConfiguration.InteractEvent::class, event)

        return event.interactionResult
    }

    companion object {
        fun setField(mojang: String, intermediary: String, obfuscated: String, block: Block, value: Any) {
            val clazz = Block::class.java
            val field: Field = try {
                clazz.getDeclaredField(mojang)
            }
            catch (e: NoSuchFieldException) {
                e.printStackTrace()

                try {
                    clazz.getDeclaredField(intermediary)
                }
                catch (f: NoSuchFieldException) {
                    f.printStackTrace()

                    try {
                        clazz.getDeclaredField(obfuscated)
                    }
                    catch (g: NoSuchFieldException) {
                        g.printStackTrace()

                        throw RuntimeException("Could not find field 'stateDefinition' (Mojang), 'field_10647' (Intermediary), 'C' (Obfuscated) in class '${clazz.name}'.")
                    }
                }
            }
            field.trySetAccessible()
            field.set(block, value)
        }
    }
}
