package io.github.takenoko4096.noctiluca.container

import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class CustomContainerMenuProvider(
    private val title: Component,
    val columnCount: Int,
    private val initializer: SimpleContainer.() -> Unit,
    private val onClick: ((Player, Int, Int, ContainerInput, CustomContainerMenu) -> Boolean)?,
    private val onSlotChanged: ((Player, Int, ItemStack, CustomContainerMenu) -> Unit)? = null,
    private val onClose: ((Player, CustomContainerMenu) -> Unit)? = null,
    private val onRemove: ((Player, CustomContainerMenu) -> Unit)? = null
) : MenuProvider {
    override fun getDisplayName() = title

    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): CustomContainerMenu {
        val menu = CustomContainerMenu(containerId, inventory, columnCount, initializer, onClick, onSlotChanged, onClose)
        CustomContainerMenu.menus[player] = menu
        return menu
    }
}
