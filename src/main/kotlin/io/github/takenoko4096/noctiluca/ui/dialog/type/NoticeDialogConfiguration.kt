package io.github.takenoko4096.noctiluca.ui.dialog.type

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.ui.dialog.AbstractDialogConfiguration
import io.github.takenoko4096.noctiluca.ui.dialog.DialogActionButtonConfiguration
import io.github.takenoko4096.noctiluca.ui.dialog.DynamicDialogEvent
import net.minecraft.core.HolderLookup
import net.minecraft.resources.Identifier
import net.minecraft.server.dialog.CommonDialogData
import net.minecraft.server.dialog.Dialog
import net.minecraft.server.dialog.NoticeDialog

class NoticeDialogConfiguration(callback: NoticeDialogConfiguration.() -> Unit) : AbstractDialogConfiguration() {
    private var action: DialogActionButtonConfiguration? = null

    init {
        callback()
    }

    fun action(callback: DialogActionButtonConfiguration.() -> Unit) {
        action = DialogActionButtonConfiguration(callback)
    }

    override fun build(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider, commonDialogData: CommonDialogData, map: MutableMap<Identifier, DynamicDialogEvent.() -> Unit>): Dialog {
        if (action == null) {
            throw IllegalStateException("'action' in notice dialog config is unset")
        }

        return NoticeDialog(
            commonDialogData,
            action!!.build(mod, map)
        )
    }
}
