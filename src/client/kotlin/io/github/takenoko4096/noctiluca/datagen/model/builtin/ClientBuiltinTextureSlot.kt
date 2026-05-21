package io.github.takenoko4096.noctiluca.datagen.model.builtin

import io.github.takenoko4096.noctiluca.render.model.builtin.NonClientBuiltinTextureSlot
import net.minecraft.client.data.models.model.TextureSlot

enum class ClientBuiltinTextureSlot(
    private val nonClient: NonClientBuiltinTextureSlot,
    private val client: TextureSlot
) {
    ALL(NonClientBuiltinTextureSlot.ALL, TextureSlot.ALL),
    PARTICLE(NonClientBuiltinTextureSlot.PARTICLE, TextureSlot.PARTICLE),
    NORTH(NonClientBuiltinTextureSlot.NORTH, TextureSlot.NORTH),
    SOUTH(NonClientBuiltinTextureSlot.SOUTH, TextureSlot.SOUTH),
    EAST(NonClientBuiltinTextureSlot.EAST, TextureSlot.EAST),
    WEST(NonClientBuiltinTextureSlot.WEST, TextureSlot.WEST),
    UP(NonClientBuiltinTextureSlot.UP, TextureSlot.UP),
    DOWN(NonClientBuiltinTextureSlot.DOWN, TextureSlot.DOWN),
    CROP(NonClientBuiltinTextureSlot.CROP, TextureSlot.CROP),
    TOP(NonClientBuiltinTextureSlot.TOP, TextureSlot.TOP),
    BOTTOM(NonClientBuiltinTextureSlot.BOTTOM, TextureSlot.BOTTOM),
    SIDE(NonClientBuiltinTextureSlot.SIDE, TextureSlot.SIDE),
    LAYER0(NonClientBuiltinTextureSlot.LAYER0, TextureSlot.LAYER0),
    LAYER1(NonClientBuiltinTextureSlot.LAYER1, TextureSlot.LAYER1),
    LAYER2(NonClientBuiltinTextureSlot.LAYER2, TextureSlot.LAYER2),
    END(NonClientBuiltinTextureSlot.END, TextureSlot.END);

    companion object {
        fun convert(nonClient: NonClientBuiltinTextureSlot): TextureSlot {
            for (entry in entries) {
                if (entry.nonClient == nonClient) return entry.client
            }

            throw IllegalStateException("Not implemented (client side)")
        }
    }
}
