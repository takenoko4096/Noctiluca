package io.github.takenoko4096.noctiluca.item.components

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.ItemEnchantments

@NoctilucaDsl
class EnchantmentsConfiguration internal constructor(mod: NoctilucaModInitializer, dataSource: HolderLookup.Provider, callback: EnchantmentsConfiguration.() -> Unit) : AbstractDataDrivenComponentConfiguration<ItemEnchantments>(mod, dataSource, DataComponents.ENCHANTMENTS) {
    private var enchantments = mutableMapOf<Holder<Enchantment>, Int>()

    init {
        callback()
    }

    fun enchant(enchantment: ResourceKey<Enchantment>, level: Int) {
        val registry = dataSource.lookupOrThrow(Registries.ENCHANTMENT)
        val holder = registry.getOrThrow(enchantment)
        enchantments[holder] = level
    }

    override fun build(): ItemEnchantments {
        val mutable = ItemEnchantments.Mutable(ItemEnchantments.EMPTY)
        enchantments.forEach {
            mutable.set(it.key, it.value)
        }
        return mutable.toImmutable()
    }
}
