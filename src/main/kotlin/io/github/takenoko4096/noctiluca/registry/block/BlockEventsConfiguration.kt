package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
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

    internal fun build(): BlockEventDispatcher {
        return BlockEventDispatcher(handlers.toSet())
    }
}