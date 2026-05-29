package io.github.takenoko4096.noctiluca.ui.container

import io.github.takenoko4096.noctiluca.container.CustomContainerMenu
import io.github.takenoko4096.noctiluca.container.CustomContainerMenuProvider
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot

class ContainerInteractionMenuProvider(
    private val interaction: ContainerInteraction,
    title: Component,
    columnCount: Int,
    initializer: SimpleContainer.() -> Unit,
    onClick: (Player, Int, Int, ContainerInput, CustomContainerMenu) -> Boolean,
    onClose: (Player, CustomContainerMenu) -> Unit
) : CustomContainerMenuProvider(title, columnCount, initializer, onClick = onClick, onSlotChanged = null, onClose = onClose) {
    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): CustomContainerMenu {
        val menu = super.createMenu(containerId, inventory, player)
        interaction.children.add(Open(player, menu))
        return menu
    }

    data class Open(internal val player: Player, internal val menu: CustomContainerMenu)
}
