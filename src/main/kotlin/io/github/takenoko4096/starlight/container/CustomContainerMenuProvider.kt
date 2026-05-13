package io.github.takenoko4096.starlight.container

import io.github.takenoko4096.starlight.ui.container.ContainerInteraction
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot

class CustomContainerMenuProvider(private val interaction: ContainerInteraction, private val title: Component, private val columnCount: Int, private val initializer: SimpleContainer.() -> Unit, private val onClick: ((Player, Int, Int, ContainerInput, NonNullList<Slot>) -> Unit)?) : MenuProvider {
    override fun getDisplayName() = title

    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        val menu = CustomContainerMenu(containerId, inventory, columnCount, initializer, onClick)
        interaction.children.add(menu)
        return menu
    }
}
