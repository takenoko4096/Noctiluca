package io.github.takenoko4096.noctiluca.ui.dialog

import net.minecraft.core.Holder
import net.minecraft.nbt.Tag
import net.minecraft.resources.Identifier
import net.minecraft.server.dialog.Dialog
import net.minecraft.world.entity.player.Player

class DialogHolder(private val dialog: Dialog, private val actions: Map<Identifier, DialogActionButtonConfiguration.DialogCustomActionButtonClickEvent.() -> Unit>, private val onEscape: (AbstractDialogConfiguration.DialogCloseLikeEvent.() -> Unit)?, private val onClose: (AbstractDialogConfiguration.DialogCloseLikeEvent.() -> Unit)?) {
    fun open(player: Player) {
        lastOpen[player] = this
        player.openDialog(Holder.direct(dialog))
    }

    companion object {
        internal val lastOpen = mutableMapOf<Player, DialogHolder>()

        fun invokeOnClick(player: Player, identifier: Identifier, payload: Tag?) {
            lastOpen[player]?.actions[identifier]?.run {
                invoke(DialogActionButtonConfiguration.DialogCustomActionButtonClickEvent(
                    player,
                    identifier,
                    payload
                ))
            }
        }

        internal fun handleOnEscape(player: Player) {
            lastOpen[player]?.onEscape?.invoke(AbstractDialogConfiguration.DialogCloseLikeEvent(player))
            handleOnClose(player)
        }

        // after onEscape
        internal fun handleOnClose(player: Player) {
            lastOpen[player]?.onClose?.invoke(AbstractDialogConfiguration.DialogCloseLikeEvent(player))
            lastOpen.remove(player)
        }
    }
}
