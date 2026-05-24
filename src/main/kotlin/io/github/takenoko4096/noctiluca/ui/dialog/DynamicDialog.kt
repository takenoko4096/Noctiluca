package io.github.takenoko4096.noctiluca.ui.dialog

import net.minecraft.core.Holder
import net.minecraft.nbt.Tag
import net.minecraft.resources.Identifier
import net.minecraft.server.dialog.Dialog
import net.minecraft.world.entity.player.Player

class DynamicDialog(private val dialog: Dialog, private val actions: Map<Identifier, DynamicDialogEvent.() -> Unit>, private val onEscape: (DynamicDialogEvent.() -> Unit)?, private val onClose: (DynamicDialogEvent.() -> Unit)?) {
    fun open(player: Player) {
        lastOpen[player] = this
        player.openDialog(Holder.direct(dialog))
    }

    companion object {
        private val lastOpen = mutableMapOf<Player, DynamicDialog>()

        fun invokeOnClick(player: Player, identifier: Identifier, payload: Tag?) {
            lastOpen[player]?.run {
                val that = this
                actions[identifier]?.run {
                    invoke(DynamicDialogEvent(player, payload, that))
                }
            }
        }

        internal fun handleOnEscape(player: Player, payload: Tag?) {
            lastOpen[player]?.run {
                onEscape?.invoke(DynamicDialogEvent(player, payload, this))
            }
            handleOnClose(player, payload)
        }

        internal fun handleOnClose(player: Player, payload: Tag?) {
            lastOpen[player]?.run {
                onClose?.invoke(DynamicDialogEvent(player, payload, this))
            }
            lastOpen.remove(player)
        }
    }
}
