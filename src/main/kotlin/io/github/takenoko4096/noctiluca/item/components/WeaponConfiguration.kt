package io.github.takenoko4096.noctiluca.item.components

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.Weapon

@NoctilucaDsl
class WeaponConfiguration(mod: NoctilucaModInitializer, callback: WeaponConfiguration.() -> Unit) : AbstractComponentConfiguration<Weapon>(mod, DataComponents.WEAPON) {
    var disableBlockingPerSeconds: Float = 0f

    var itemDamagePerAttack: Int = 1

    init {
        callback()
    }

    override fun build(): Weapon {
        return Weapon(
            itemDamagePerAttack,
            disableBlockingPerSeconds
        )
    }
}