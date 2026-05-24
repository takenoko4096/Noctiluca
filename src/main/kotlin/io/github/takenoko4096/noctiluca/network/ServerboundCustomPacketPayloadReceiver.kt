package io.github.takenoko4096.noctiluca.network

import io.github.takenoko4096.noctiluca.ui.dialog.DynamicDialog
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object ServerboundCustomPacketPayloadReceiver {
    fun escapeDialogPayload(payload: ServerboundDialogEscapePayload, context: ServerPlayNetworking.Context) {
        DynamicDialog.handleOnEscape(context.player(), payload.payload.orElse(null))
    }

    fun closeDialogPayload(payload: ServerboundDialogClosePayload, context: ServerPlayNetworking.Context) {
        DynamicDialog.handleOnClose(context.player(), payload.payload.orElse(null))
    }
}
