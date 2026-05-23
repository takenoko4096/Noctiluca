package io.github.takenoko4096.noctiluca.ui.dialog.type

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.ui.dialog.AbstractDialogConfiguration
import net.minecraft.core.HolderLookup
import net.minecraft.server.dialog.ActionButton
import net.minecraft.server.dialog.CommonButtonData
import net.minecraft.server.dialog.ConfirmationDialog
import net.minecraft.server.dialog.Dialog

class ConfirmationDialogConfiguration(callback: ConfirmationDialogConfiguration.() -> Unit) : AbstractDialogConfiguration() {
    init {
        callback()
    }

    override fun build(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider): Dialog {
        return ConfirmationDialog(
            buildCommonDialogData(mod, registryAccess),
            ActionButton(

            )
        )
    }
}
