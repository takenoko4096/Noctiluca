package io.github.takenoko4096.noctiluca.item.components

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.text.SectionComponentBuilder
import io.github.takenoko4096.noctiluca.text.component
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.component.ItemLore

@NoctilucaDsl
class LoreConfiguration(mod: NoctilucaModInitializer, callback: LoreConfiguration.() -> Unit) : AbstractComponentConfiguration<ItemLore>(mod, DataComponents.LORE) {
    private val lines = mutableListOf<Component>()

    init {
        callback()
    }

    fun line(builder: SectionComponentBuilder.() -> Unit) {
        line(component(builder))
    }

    fun line(component: Component) {
        lines.add(component)
    }

    override fun build(): ItemLore {
        return ItemLore(lines.toList())
    }
}
