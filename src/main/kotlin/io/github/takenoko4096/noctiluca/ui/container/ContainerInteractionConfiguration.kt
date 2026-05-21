package io.github.takenoko4096.noctiluca.ui.container

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.text.SectionComponentBuilder
import io.github.takenoko4096.noctiluca.text.component
import net.minecraft.network.chat.Component

@NoctilucaDsl
class ContainerInteractionConfiguration(callback: ContainerInteractionConfiguration.() -> Unit) {
    internal var title: Component = Component.empty()

    internal var contents: ContainerInteractiveContents = ContainerInteractiveContents(1) {}

    init {
        callback()
    }

    fun title(builder: SectionComponentBuilder.() -> Unit) {
        title = component(builder)
    }

    fun contents(columns: Int, callback: ContainerInteractiveContents.() -> Unit) {
        contents = ContainerInteractiveContents(columns, callback)
    }
}
