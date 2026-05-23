package io.github.takenoko4096.noctiluca.ui.dialog

import io.github.takenoko4096.noctiluca.text.SectionComponentBuilder
import io.github.takenoko4096.noctiluca.text.component
import net.minecraft.network.chat.Component
import net.minecraft.server.dialog.ActionButton
import net.minecraft.server.dialog.CommonButtonData
import net.minecraft.server.dialog.action.Action
import net.minecraft.server.dialog.action.CommandTemplate
import java.util.Optional

abstract class DialogActionButtonConfiguration {
    private var label: Component = Component.empty()

    private var tooltip: Component? = null

    var width: Int = 150

    fun label(callback: SectionComponentBuilder.() -> Unit) {
        label = component(callback)
    }

    fun tooltip(callback: SectionComponentBuilder.() -> Unit) {
        tooltip = component(callback)
    }

    protected fun buildCommonButtonData(): CommonButtonData {
        return CommonButtonData(
            label,
            tooltip?.let { Optional.of(it) } ?: Optional.empty(),
            width
        )
    }

    abstract fun build(): ActionButton

    class CommandTemplateConfiguration : DialogActionButtonConfiguration() {
        override fun build(): ActionButton {
            // CommandTemplate() nbtとしてカスタムコマンドの引数に渡す {id: 0, inputs: [{type: boolean, key: foo, value: 0b}]} みたいな
        }
    }
}
