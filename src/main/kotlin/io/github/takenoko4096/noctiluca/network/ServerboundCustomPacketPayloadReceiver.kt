package io.github.takenoko4096.noctiluca.network

import io.github.takenoko4096.noctiluca.ui.dialog.DialogHolder
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object ServerboundCustomPacketPayloadReceiver {
    fun escapeDialogPayload(payload: ServerboundDialogEscapePayload, context: ServerPlayNetworking.Context) {
        DialogHolder.handleOnEscape(context.player())
    }

    fun closeDialogPayload(payload: ServerboundDialogClosePayload, context: ServerPlayNetworking.Context) {
        DialogHolder.handleOnClose(context.player())
    }
}
