package io.github.takenoko4096.noctiluca.network

import io.github.takenoko4096.noctiluca.Noctiluca
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

object ServerboundDialogEscapePayload : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<ServerboundDialogEscapePayload> {
        return TYPE
    }

    val CODEC: StreamCodec<ByteBuf, ServerboundDialogEscapePayload> = StreamCodec.unit(ServerboundDialogEscapePayload)

    val TYPE = CustomPacketPayload.Type<ServerboundDialogEscapePayload>(Noctiluca.identifierOf("escape_dialog"))
}
