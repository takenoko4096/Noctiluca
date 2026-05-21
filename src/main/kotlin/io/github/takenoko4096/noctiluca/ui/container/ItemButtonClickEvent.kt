package io.github.takenoko4096.noctiluca.ui.container

import io.github.takenoko4096.noctiluca.Noctiluca
import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.container.CustomContainerMenu
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

@NoctilucaDsl
class ItemButtonClickEvent internal constructor(
    val interaction: ContainerInteraction,
    val player: Player,
    val button: ItemButton,
    val copiedButtonItemStack: ItemStack
) {
    fun indexAt(horizontalIndex: Int, verticalIndex: Int): Int {
        return horizontalIndex + CustomContainerMenu.SLOTS_PER_ROW * verticalIndex
    }

    fun lastRowIndex(): Int = interaction.contents.columnCount - 1

    fun lastColumnIndex(): Int = CustomContainerMenu.SLOTS_PER_ROW - 1

    fun horizontalCenterIndex(): Int = (CustomContainerMenu.SLOTS_PER_ROW - 1) / 2

    fun close() {
        if (player is ServerPlayer) {
            player.closeContainer()
        }
        else {
            Noctiluca.logger.warn("event was called from client side; cannot close window")
        }
    }
}
