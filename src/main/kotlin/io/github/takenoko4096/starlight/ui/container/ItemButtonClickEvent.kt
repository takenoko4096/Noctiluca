package io.github.takenoko4096.starlight.ui.container

import io.github.takenoko4096.starlight.Noctiluca
import io.github.takenoko4096.starlight.StarlightDSL
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

@StarlightDSL
class ItemButtonClickEvent internal constructor(
    val interaction: ContainerInteraction,
    val player: Player,
    val button: ItemButton,
    val copiedButtonItemStack: ItemStack
) {
    fun close() {
        if (player is ServerPlayer) {
            player.closeContainer()
        }
        else {
            Noctiluca.logger.warn("event was called from client side; cannot close window")
        }
    }
}
