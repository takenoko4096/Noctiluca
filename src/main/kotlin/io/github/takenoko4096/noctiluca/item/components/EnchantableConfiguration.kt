package io.github.takenoko4096.noctiluca.item.components

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.enchantment.Enchantable

@NoctilucaDsl
class EnchantableConfiguration(mod: NoctilucaModInitializer, callback: EnchantableConfiguration.() -> Unit) : AbstractComponentConfiguration<Enchantable>(mod, DataComponents.ENCHANTABLE) {
    var enchantmentAptitude: Int? = null

    init {
        callback()
    }

    override fun build(): Enchantable {
        if (enchantmentAptitude == null) {
            throw IllegalStateException("'enchantment aptitude' is unset")
        }

        return Enchantable(enchantmentAptitude!!)
    }
}
