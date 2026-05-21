package io.github.takenoko4096.noctiluca.item.components

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import net.minecraft.core.component.DataComponents
import net.minecraft.world.food.FoodProperties

@NoctilucaDsl
class FoodConfiguration(mod: NoctilucaModInitializer, callback: FoodConfiguration.() -> Unit) : AbstractComponentConfiguration<FoodProperties>(mod, DataComponents.FOOD) {
    var nutrition: Int = 0

    var saturation: Float = 0f

    var canAlwaysEat: Boolean = false

    init {
        callback()
    }

    override fun build(): FoodProperties {
        return FoodProperties(
            nutrition,
            saturation,
            canAlwaysEat
        )
    }
}
