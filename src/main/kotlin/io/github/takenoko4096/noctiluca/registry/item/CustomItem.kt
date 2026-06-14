package io.github.takenoko4096.noctiluca.registry.item

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level

abstract class CustomItem(properties: Properties, private val eventDispatcher: ItemEventsConfiguration.ItemEventDispatcher) : Item(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
       if (level.isClientSide) {
           return InteractionResult.PASS
       }

        val event = ItemEventsConfiguration.InteractEvent(level, player, hand, null)
        eventDispatcher.dispatch(ItemEventsConfiguration.InteractEvent::class, event)

        return event.interactionResult ?: super.use(level, player, hand)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        if (context.level.isClientSide) {
            return InteractionResult.PASS
        }

        val event = ItemEventsConfiguration.InteractBlockEvent(context, null)
        eventDispatcher.dispatch(ItemEventsConfiguration.InteractBlockEvent::class, event)

        return event.interactionResult ?: super.useOn(context)
    }
}
