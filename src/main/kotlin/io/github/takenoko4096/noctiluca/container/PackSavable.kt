package io.github.takenoko4096.noctiluca.container

import io.github.takenoko4096.mojangson.MojangsonPath
import io.github.takenoko4096.mojangson.MojangsonValueTypes
import io.github.takenoko4096.mojangson.values.MojangsonCompound
import io.github.takenoko4096.mojangson.values.MojangsonList
import io.github.takenoko4096.noctiluca.nbt.toCompoundTag
import io.github.takenoko4096.noctiluca.nbt.toMojangsonCompound
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class PackSavable(title: Component, private val columnCount: Int, private val compound: MojangsonCompound = MojangsonCompound()) {
    private val provider = CustomContainerMenuProvider(
        title = title,
        columnCount = columnCount,
        initializer = ::onInitialize,
        onClick = null,
        onSlotChanged = ::onSlotChanged,
        onClose = null
    )

    private fun onInitialize(container: SimpleContainer) {
        for (compound in compound[SLOTS_PATH, MojangsonValueTypes.LIST].typed(MojangsonValueTypes.COMPOUND)) {
            val index = compound.get("slot", MojangsonValueTypes.INT).value
            val compoundTag = compound.get("item_stack", MojangsonValueTypes.COMPOUND).toCompoundTag()
            val itemStack = ItemStack.CODEC.decode(NbtOps.INSTANCE, compoundTag).getOrThrow(::IllegalStateException).first
            container.setItem(index, itemStack)
        }
    }

    private fun onSlotChanged(player: Player, index: Int, itemStack: ItemStack, slots: NonNullList<Slot>) {
        compound[SLOTS_PATH] = MojangsonList.valueOf(slots.map {
            var tag = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, it.item).getOrThrow(::IllegalStateException)

            if (tag !is CompoundTag) tag = CompoundTag()

            MojangsonCompound.valueOf(mapOf(
                "slot" to it.index,
                "item_stack" to tag.toMojangsonCompound()
            ))
        })
    }

    fun open(player: Player) {
        player.openMenu(provider)
    }

    companion object {
        val SLOTS_PATH = MojangsonPath.of("slots")
    }
}
