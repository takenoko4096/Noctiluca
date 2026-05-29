package io.github.takenoko4096.noctiluca.ui.dialog

import io.github.takenoko4096.mojangson.MojangsonValue
import io.github.takenoko4096.mojangson.MojangsonValueType
import io.github.takenoko4096.mojangson.MojangsonValueTypes
import io.github.takenoko4096.mojangson.values.MojangsonCompound
import io.github.takenoko4096.noctiluca.nbt.toMojangsonCompound
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.entity.player.Player

class DynamicDialogEvent internal constructor(val player: Player, val response: DynamicDialogResponse, val dialog: DynamicDialog) {
    internal constructor(player: Player, payload: Tag?, dialog: DynamicDialog) : this(
        player,
        if (payload is CompoundTag) {
            DynamicDialogResponse(payload.toMojangsonCompound())
        }
        else {
            DynamicDialogResponse(MojangsonCompound())
        },
        dialog
    )

    class DynamicDialogResponse(private val compound: MojangsonCompound) {
        private fun <T : MojangsonValue<*>> getWrappedNullable(key: String, type: MojangsonValueType<T>): T? {
            return if (compound.has(key) && compound.getTypeOf(key) == type) compound.get(key, type) else null
        }

        fun float(key: String) = getWrappedNullable(key, MojangsonValueTypes.FLOAT)?.floatValue()
        fun boolean(key: String) = getWrappedNullable(key, MojangsonValueTypes.BYTE)?.booleanValue()
        fun string(key: String) = getWrappedNullable(key, MojangsonValueTypes.STRING)?.value

        fun float(key: String, default: Float) = float(key) ?: default
        fun boolean(key: String, default: Boolean) = boolean(key) ?: default
        fun string(key: String, default: String) = string(key) ?: default

        fun toCompound(): MojangsonCompound {
            return compound.copy() as MojangsonCompound
        }
    }
}
