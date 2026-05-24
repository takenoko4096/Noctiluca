package io.github.takenoko4096.noctiluca.network

import io.github.takenoko4096.noctiluca.Noctiluca
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.Tag
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import java.util.Optional

class ServerboundDialogClosePayload(val payload: Optional<Tag>) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<ServerboundDialogClosePayload> {
        return TYPE
    }

    companion object {
        val CODEC: StreamCodec<ByteBuf, ServerboundDialogClosePayload> = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.TAG),
            ServerboundDialogClosePayload::payload,
            ::ServerboundDialogClosePayload
        )

        val TYPE = CustomPacketPayload.Type<ServerboundDialogClosePayload>(Noctiluca.identifierOf("close_dialog"))
    }
}
