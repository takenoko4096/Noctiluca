package io.github.takenoko4096.noctiluca.container

import io.github.takenoko4096.mojangson.MojangsonPath
import io.github.takenoko4096.mojangson.MojangsonValueTypes
import io.github.takenoko4096.mojangson.values.MojangsonCompound
import io.github.takenoko4096.mojangson.values.MojangsonList
import io.github.takenoko4096.noctiluca.nbt.toCompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class PackSavable(title: Component, columnCount: Int, private val compound: MojangsonCompound = MojangsonCompound(), private val onUpdate: PackSavable.() -> Unit) {
    private val provider = Provider(title, columnCount, this)

    private val menus = mutableMapOf<Player, CustomContainerMenu>()

    private fun onInitialize(container: SimpleContainer) {
        if (!compound.has(ITEMS_PATH)) {
            compound.set(ITEMS_PATH, MojangsonList())
        }

        for (slot in compound[ITEMS_PATH, MojangsonValueTypes.LIST].typed(MojangsonValueTypes.COMPOUND)) {
            val index = slot.get("Slot", MojangsonValueTypes.BYTE).value
            val itemStack = ItemStack.MAP_CODEC.decoder().decode(NbtOps.INSTANCE, slot.toCompoundTag()).getOrThrow(::IllegalStateException).first
            container.setItem(index.toInt(), itemStack)
        }
    }

    private fun onSlotChanged(player: Player, index: Int, itemStack: ItemStack, menu: CustomContainerMenu) {
        compound[ITEMS_PATH] = menu.serializeContents()
        for ((_, menu) in menus) {
            menu.setItem(index, menu.incrementStateId(), itemStack)
            menu.broadcastChanges()
        }
        onUpdate()
    }

    private fun onRemove(player: Player, menu: CustomContainerMenu) {
        menus.remove(player)
    }

    fun getSerializedContents(): MojangsonCompound {
        return compound.copy() as MojangsonCompound
    }

    fun open(player: Player) {
        player.openMenu(provider)
    }

    class Provider(title: Component, columnCount: Int, private val pack: PackSavable) : CustomContainerMenuProvider(
        title = title,
        columnCount = columnCount,
        initializer = pack::onInitialize,
        onClick = null,
        onSlotChanged = pack::onSlotChanged,
        onClose = null,
        onRemove = pack::onRemove
    ) {
        override fun createMenu(containerId: Int, inventory: Inventory, player: Player): CustomContainerMenu {
            val menu = super.createMenu(containerId, inventory, player)
            pack.menus[player] = menu
            return menu
        }
    }

    companion object {
        val ITEMS_PATH = MojangsonPath.of("Items")
    }
}
