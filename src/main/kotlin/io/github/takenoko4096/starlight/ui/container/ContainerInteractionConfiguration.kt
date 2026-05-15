package io.github.takenoko4096.starlight.ui.container

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.text.SectionComponentBuilder
import io.github.takenoko4096.starlight.text.component
import net.minecraft.network.chat.Component

@StarlightDSL
class ContainerInteractionConfiguration(callback: ContainerInteractionConfiguration.() -> Unit) {
    internal var title: Component = Component.empty()

    internal var contents: ContainerInteractionContents = ContainerInteractionContents(1) {}

    init {
        callback()
    }

    fun title(builder: SectionComponentBuilder.() -> Unit) {
        title = component(builder)
    }

    fun contents(columns: Int, callback: ContainerInteractionContents.() -> Unit) {
        contents = ContainerInteractionContents(columns, callback)
    }
}
