package io.github.takenoko4096.noctiluca.network

import io.github.takenoko4096.noctiluca.Noctiluca
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.Tag
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import java.util.Optional

class ServerboundDialogEscapePayload(val payload: Optional<Tag>) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<ServerboundDialogEscapePayload> {
        return TYPE
    }

    companion object {
        val CODEC: StreamCodec<ByteBuf, ServerboundDialogEscapePayload> = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.TAG),
            ServerboundDialogEscapePayload::payload,
            ::ServerboundDialogEscapePayload
        )

        val TYPE = CustomPacketPayload.Type<ServerboundDialogEscapePayload>(Noctiluca.identifierOf("escape_dialog"))
    }
}
