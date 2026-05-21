package io.github.takenoko4096.noctiluca.registry.item

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.registry.StarlightRegistry
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import kotlin.collections.set

class ModItemRegistry(mod: NoctilucaModInitializer) : StarlightRegistry(mod) {
    private val configurations = mutableSetOf<ModItemConfiguration>()

    private val items = mutableMapOf<ResourceKey<Item>, Item>()

    fun register(identifier: String, configuration: ModItemConfiguration.() -> Unit): Item {
        val o = ModItemConfiguration(this, identifier)
        o.configuration()
        val item = o.register()
        configurations.add(o)
        items[o.itemResourceKey] = item
        return item
    }

    fun getItem(resourceKey: ResourceKey<Item>): Item {
        return items[resourceKey] ?: throw IllegalArgumentException("ブロック '${resourceKey.identifier()}' が Modアイテムレジストリに見つかりませんでした: ${items.keys.map { it.identifier() }}")
    }

    fun getConfigurations(): Set<ModItemConfiguration> {
        return configurations.toSet()
    }
}
