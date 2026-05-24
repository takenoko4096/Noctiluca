package io.github.takenoko4096.noctiluca.ui.dialog.type

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.ui.dialog.AbstractDialogConfiguration
import io.github.takenoko4096.noctiluca.ui.dialog.DialogActionButtonConfiguration
import io.github.takenoko4096.noctiluca.ui.dialog.DynamicDialogEvent
import net.minecraft.core.HolderLookup
import net.minecraft.resources.Identifier
import net.minecraft.server.dialog.CommonDialogData
import net.minecraft.server.dialog.Dialog
import net.minecraft.server.dialog.MultiActionDialog
import java.util.Optional

@NoctilucaDsl
class MultiActionDialogConfiguration(callback: MultiActionDialogConfiguration.() -> Unit) : AbstractDialogConfiguration() {
    private var actions: List<DialogActionButtonConfiguration> = listOf()

    private var exitAction: DialogActionButtonConfiguration? = null

    var columns: Int = 2

    init {
        callback()
    }

    fun actions(callback: ActionsConfiguration.() -> Unit) {
        actions = ActionsConfiguration(callback).actions
    }

    fun exitAction(callback: DialogActionButtonConfiguration.() -> Unit) {
        exitAction = DialogActionButtonConfiguration(callback)
    }

    override fun build(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider, commonDialogData: CommonDialogData, map: MutableMap<Identifier, DynamicDialogEvent.() -> Unit>): Dialog {
        if (actions.isEmpty()) {
            throw IllegalStateException("'actions' in multi-action dialog config is empty")
        }

        return MultiActionDialog(
            commonDialogData,
            actions.map { it.build(mod, map) },
            exitAction?.let { Optional.of(it.build(mod, map)) } ?: Optional.empty(),
            columns
        )
    }

    @NoctilucaDsl
    class ActionsConfiguration internal constructor(callback: ActionsConfiguration.() -> Unit) {
        internal val actions = mutableListOf<DialogActionButtonConfiguration>()

        init {
            callback()
        }

        fun action(callback: DialogActionButtonConfiguration.() -> Unit) {
            actions.add(DialogActionButtonConfiguration(callback))
        }
    }
}
