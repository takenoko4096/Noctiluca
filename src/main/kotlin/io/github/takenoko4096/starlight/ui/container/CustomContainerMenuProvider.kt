package io.github.takenoko4096.starlight.ui.container

import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerInput

class CustomContainerMenuProvider(private val title: Component, private val columnCount: Int, private val initializer: SimpleContainer.() -> Unit, private val onClick: ((Player, Int, Int, ContainerInput) -> Unit)?) : MenuProvider {
    override fun getDisplayName() = title

    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return CustomContainerMenu(containerId, inventory, columnCount, initializer, onClick)
    }
}
