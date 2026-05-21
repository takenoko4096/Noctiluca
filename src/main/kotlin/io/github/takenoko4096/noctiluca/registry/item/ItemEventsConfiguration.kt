package io.github.takenoko4096.noctiluca.registry.item

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import kotlin.reflect.KClass

@NoctilucaDsl
class ItemEventsConfiguration internal constructor() {
    private val handlers = mutableSetOf<ItemEventHandler<*>>()

    fun onUse(callback: InteractEvent.() -> Unit) {
        handlers.add(ItemEventHandler(
            InteractEvent::class,
            callback
        ))
    }

    abstract class ItemEvent internal constructor() {}

    class ItemEventHandler<T : ItemEvent> internal constructor(
        val clazz: KClass<T>,
        val callback: T.() -> Unit
    )

    class ItemEventDispatcher internal constructor(private val set: Set<ItemEventHandler<*>>) {
        internal fun <T : ItemEvent> dispatch(clazz: KClass<T>, event: T) {
            set.filter { it.clazz == clazz }
                .forEach { (it.callback as T.() -> Unit)(event) }
        }
    }

    class InteractEvent internal constructor(
        val level: Level,
        val player: Player,
        val hand: InteractionHand
    ) : ItemEvent() {

    }

    internal fun build(): ItemEventDispatcher {
        return ItemEventDispatcher(handlers.toSet())
    }
}
