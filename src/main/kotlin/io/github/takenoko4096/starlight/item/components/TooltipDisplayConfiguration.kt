package io.github.takenoko4096.starlight.item.components

import io.github.takenoko4096.starlight.NoctilucaModInitializer
import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.item.ItemComponent
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.TooltipDisplay

@StarlightDSL
class TooltipDisplayConfiguration internal constructor(mod: NoctilucaModInitializer, callback: TooltipDisplayConfiguration.() -> Unit) : AbstractComponentConfiguration<TooltipDisplay>(mod, DataComponents.TOOLTIP_DISPLAY) {
    var hideTooltip: Boolean = false

    private val hiddenComponents = sortedSetOf<DataComponentType<*>>()

    init {
        callback()
    }

    fun <T : Any> hideComponent(type: DataComponentType<T>) {
        hiddenComponents.add(type)
    }

    override fun build(): TooltipDisplay {
        return TooltipDisplay(hideTooltip, hiddenComponents)
    }
}
