package io.github.takenoko4096.starlight.ui.container

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.text.SectionComponentBuilder
import io.github.takenoko4096.starlight.text.component
import net.minecraft.network.chat.Component
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerInput

@StarlightDSL
class CustomContainerMenuBuilder internal constructor(callback: CustomContainerMenuBuilder.() -> Unit) {
    private var title: Component = Component.empty()

    private var columnCount: Int = 3

    private var initializer: SimpleContainer.() -> Unit = {}

    private var onClick: ((Player, Int, Int, ContainerInput) -> Unit)? = null

    init {
        callback()
    }

    fun title(builder: SectionComponentBuilder.() -> Unit) {
        title = component(builder)
    }

    fun contents(columnCount: Int, initializer: SimpleContainer.() -> Unit) {
        this.columnCount = columnCount
        this.initializer = initializer
    }

    fun onClick(callback: (Player, Int, Int, ContainerInput) -> Unit) {
        onClick = callback
    }

    fun build(): CustomContainerMenuProvider {
        return CustomContainerMenuProvider(title, columnCount, initializer, onClick)
    }
}
