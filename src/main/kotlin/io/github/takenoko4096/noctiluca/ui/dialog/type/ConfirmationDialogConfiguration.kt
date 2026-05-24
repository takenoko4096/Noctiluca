package io.github.takenoko4096.noctiluca.ui.dialog.type

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.ui.dialog.AbstractDialogConfiguration
import io.github.takenoko4096.noctiluca.ui.dialog.DialogActionButtonConfiguration
import io.github.takenoko4096.noctiluca.ui.dialog.DynamicDialogEvent
import net.minecraft.core.HolderLookup
import net.minecraft.resources.Identifier
import net.minecraft.server.dialog.CommonDialogData
import net.minecraft.server.dialog.ConfirmationDialog
import net.minecraft.server.dialog.Dialog

class ConfirmationDialogConfiguration(callback: ConfirmationDialogConfiguration.() -> Unit) : AbstractDialogConfiguration() {
    private var yes: DialogActionButtonConfiguration? = null

    private var no: DialogActionButtonConfiguration? = null

    init {
        callback()
    }

    fun yes(callback: DialogActionButtonConfiguration.() -> Unit) {
        yes = DialogActionButtonConfiguration(callback)
    }

    fun no(callback: DialogActionButtonConfiguration.() -> Unit) {
        no = DialogActionButtonConfiguration(callback)
    }

    override fun build(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider, commonDialogData: CommonDialogData, map: MutableMap<Identifier, DynamicDialogEvent.() -> Unit>): Dialog {
        if (yes == null) {
            throw IllegalStateException("'yes' in confirmation dialog config is unset")
        }

        if (no == null) {
            throw IllegalStateException("'no' in confirmation dialog config is unset")
        }

        return ConfirmationDialog(
            commonDialogData,
            yes!!.build(mod, map),
            no!!.build(mod, map)
        )
    }
}
