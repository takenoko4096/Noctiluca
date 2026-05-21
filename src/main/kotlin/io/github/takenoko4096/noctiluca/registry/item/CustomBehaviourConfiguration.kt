package io.github.takenoko4096.noctiluca.registry.item

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.world.item.Item

@NoctilucaDsl
class CustomBehaviourConfiguration {
    private var eventDispatcher: ItemEventsConfiguration.ItemEventDispatcher = ItemEventsConfiguration.ItemEventDispatcher(setOf())

    fun events(callback: ItemEventsConfiguration.() -> Unit) {
        val bec = ItemEventsConfiguration()
        bec.callback()
        eventDispatcher = bec.build()
    }

    internal fun build(): (Item.Properties) -> CustomItem {
        val evs = eventDispatcher

        return {
            object : CustomItem(it, evs) {

            }
        }
    }
}
