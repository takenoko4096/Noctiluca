package io.github.takenoko4096.noctiluca.ui.dialog

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.text.SectionComponentBuilder
import io.github.takenoko4096.noctiluca.text.component
import net.minecraft.core.HolderLookup
import net.minecraft.network.chat.Component
import net.minecraft.server.dialog.CommonDialogData
import net.minecraft.server.dialog.Dialog
import net.minecraft.server.dialog.DialogAction
import net.minecraft.server.dialog.Input
import java.util.Optional

abstract class AbstractDialogConfiguration {
    private var name: Component = Component.empty()

    private var title: Component? = null

    private var bodyConfiguration: DialogBodyConfiguration.() -> Unit = {}

    private var inputs: List<Input> = listOf()

    var after: DialogAction = DialogAction.CLOSE

    var pause: Boolean = true

    var canCloseWithEscape: Boolean = true

    fun name(callback: SectionComponentBuilder.() -> Unit) {
        name = component(callback)
    }

    fun title(callback: SectionComponentBuilder.() -> Unit) {
        title = component(callback)
    }

    fun body(callback: DialogBodyConfiguration.() -> Unit) {
        bodyConfiguration = callback
    }

    fun inputs(callback: DialogInputConfiguration.() -> Unit) {
        inputs = DialogInputConfiguration(callback).build()
    }

    protected fun buildCommonDialogData(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider): CommonDialogData {
        return CommonDialogData(
            title ?: name,
            Optional.of(name),
            canCloseWithEscape,
            pause,
            after,
            DialogBodyConfiguration(bodyConfiguration).build(mod, registryAccess),
            inputs
        )
    }

    abstract fun build(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider): Dialog
}
