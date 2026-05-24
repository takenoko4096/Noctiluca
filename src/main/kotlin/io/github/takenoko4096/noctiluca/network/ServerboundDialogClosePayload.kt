package io.github.takenoko4096.noctiluca.network

import io.github.takenoko4096.noctiluca.Noctiluca
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

object ServerboundDialogClosePayload : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<ServerboundDialogClosePayload> {
        return TYPE
    }

    val CODEC: StreamCodec<ByteBuf, ServerboundDialogClosePayload> = StreamCodec.unit(ServerboundDialogClosePayload)

    val TYPE = CustomPacketPayload.Type<ServerboundDialogClosePayload>(Noctiluca.identifierOf("close_dialog"))
}
