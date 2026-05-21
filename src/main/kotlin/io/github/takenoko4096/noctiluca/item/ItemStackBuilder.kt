package io.github.takenoko4096.noctiluca.item

import io.github.takenoko4096.noctiluca.ServerContainer
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import net.minecraft.core.HolderLookup
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate

class ItemStackBuilder internal constructor(private val item: Item, private val amount: Int = 1, private val callback: ItemComponents.() -> Unit = {}) {
    fun build(mod: NoctilucaModInitializer, dataSource: HolderLookup.Provider?): ItemStack {
        val template = ItemStackTemplate(item, amount)

        val components = ItemComponents(mod, dataSource, callback)

        return components.createStack(template)
    }

    fun build(data: ServerContainer): ItemStack {
        return build(data.mod, data.server.registryAccess())
    }

    fun build(mod: NoctilucaModInitializer): ItemStack {
        return build(mod, null)
    }
}
