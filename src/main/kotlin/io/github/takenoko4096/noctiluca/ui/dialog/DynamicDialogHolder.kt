package io.github.takenoko4096.noctiluca.ui.dialog

import io.github.takenoko4096.noctiluca.ui.dialog.type.ConfirmationDialogConfiguration
import io.github.takenoko4096.noctiluca.ui.dialog.type.MultiActionDialogConfiguration
import io.github.takenoko4096.noctiluca.ui.dialog.type.NoticeDialogConfiguration
import net.minecraft.core.HolderLookup
import net.minecraft.world.entity.player.Player

class DynamicDialogHolder private constructor(private val configuration: AbstractDialogConfiguration) {
    fun buildOpen(player: Player) {
        configuration.create(player.registryAccess()).open(player)
    }

    companion object {
        fun notice(callback: NoticeDialogConfiguration.() -> Unit): DynamicDialogHolder {
            return DynamicDialogHolder(NoticeDialogConfiguration(callback))
        }

        fun confirmation(callback: ConfirmationDialogConfiguration.() -> Unit): DynamicDialogHolder {
            return DynamicDialogHolder(ConfirmationDialogConfiguration(callback))
        }

        fun multiAction(callback: MultiActionDialogConfiguration.() -> Unit): DynamicDialogHolder {
            return DynamicDialogHolder(MultiActionDialogConfiguration(callback))
        }
    }
}
