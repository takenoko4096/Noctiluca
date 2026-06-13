package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.math.Position3i
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.InsideBlockEffectApplier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import kotlin.reflect.KClass

@NoctilucaDsl
class BlockEventsConfiguration internal constructor() {
    private val handlers = mutableSetOf<BlockEventHandler<*>>()

    fun onFallOn(callback: FallOnEvent.() -> Unit) {
        handlers.add(BlockEventHandler(
            FallOnEvent::class,
            callback
        ))
    }

    fun onStepOn(callback: StepOnEvent.() -> Unit) {
        handlers.add(BlockEventHandler(
            StepOnEvent::class,
            callback
        ))
    }

    fun onExplosionHit(callback: ExplosionHitEvent.() -> Unit) {
        handlers.add(BlockEventHandler(
            ExplosionHitEvent::class,
            callback
        ))
    }

    fun onInteract(callback: InteractEvent.() -> Unit) {
        handlers.add(BlockEventHandler(
            InteractEvent::class,
            callback
        ))
    }

    fun onUpdate(callback: UpdateEvent.() -> Unit) {
        handlers.add(BlockEventHandler(
            UpdateEvent::class,
            callback
        ))
    }

    fun onAnimateTick(callback: AnimateTickEvent.() -> Unit) {
        handlers.add(BlockEventHandler(
            AnimateTickEvent::class,
            callback
        ))
    }

    fun onEntityInsideBlock(callback: EntityInsideEvent.() -> Unit) {
        handlers.add(BlockEventHandler(
            EntityInsideEvent::class,
            callback
        ))
    }

    fun tick(callback: TickEvent.() -> Unit) {
        handlers.add(BlockEventHandler(
            TickEvent::class,
            callback
        ))
    }

    abstract class BlockEvent internal constructor() {}

    class BlockEventHandler<T : BlockEvent> internal constructor(
        val clazz: KClass<T>,
        val callback: T.() -> Unit
    )

    class BlockEventDispatcher internal constructor(private val set: Set<BlockEventHandler<*>>) {
        internal fun <T : BlockEvent> dispatch(clazz: KClass<T>, event: T) {
            set.filter { it.clazz == clazz }
                .forEach { (it.callback as T.() -> Unit)(event) }
        }
    }

    class FallOnEvent internal constructor(
        val level: Level,
        val blockState: BlockState,
        val blockPos: BlockPos,
        val entity: Entity,
        var distance: Double,
        var causeDamage: Boolean = true
    ) : BlockEvent()

    class StepOnEvent internal constructor(
        val level: Level,
        val blockState: BlockState,
        val blockPos: BlockPos,
        val entity: Entity
    ) : BlockEvent()

    class ExplosionHitEvent internal constructor(
        val serverLevel: ServerLevel,
        val blockState: BlockState,
        val blockPos: BlockPos,
        val explosion: Explosion,
        var ignore: Boolean = false,
        var dropHandle: (ExplosionDrop) -> Unit
    ) : BlockEvent() {
        class ExplosionDrop internal constructor(
            val itemStack: ItemStack,
            val blockPos: BlockPos
        )
    }

    class InteractEvent internal constructor(
        val level: Level,
        val blockState: BlockState,
        val blockPos: BlockPos,
        val player: Player,
        val blockHitResult: BlockHitResult,
        val interactionHand: InteractionHand,
        val itemStack: ItemStack?,
        internal var interactionResult: InteractionResult
    ) : BlockEvent() {

    }

    class UpdateEvent internal constructor(
        val level: Level,
        val blockState: BlockState,
        val blockPos: BlockPos,
        val directionToNeighbour: Direction,
        val neighbourPos: BlockPos,
        val neighbourState: BlockState,
        val ticks: ScheduledTickAccess,
        val random: RandomSource
    ) : BlockEvent() {
        var finalBlockState: BlockState? = null
    }

    class AnimateTickEvent internal constructor(
        val level: Level,
        val blockState: BlockState,
        val position: Position3i,
        val randomSource: RandomSource
    ) : BlockEvent()

    class EntityInsideEvent internal constructor(
        val level: Level,
        val position: Position3i,
        val blockState: BlockState,
        val entity: Entity,
        val insideBlockEffectApplier: InsideBlockEffectApplier,
        val isPrecise: Boolean
    ) : BlockEvent()

    class TickEvent internal constructor(
        val level: ServerLevel,
        val position: Position3i,
        val blockState: BlockState,
        val randomSource: RandomSource
    ) : BlockEvent()

    internal fun build(): BlockEventDispatcher {
        return BlockEventDispatcher(handlers.toSet())
    }
}