package io.github.takenoko4096.noctiluca.ui.dialog

import io.github.takenoko4096.mojangson.values.MojangsonCompound
import io.github.takenoko4096.noctiluca.nbt.toMojangsonCompound
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.entity.player.Player

class DynamicDialogEvent internal constructor(val player: Player, val payload: MojangsonCompound, val dialog: DynamicDialog) {
    internal constructor(player: Player, payload: Tag?, dialog: DynamicDialog) : this(player, when (payload) {
        is CompoundTag -> payload.toMojangsonCompound()
        else -> MojangsonCompound()
    }, dialog)
}
