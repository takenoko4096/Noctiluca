package io.github.takenoko4096.noctiluca.ui.container

import net.minecraft.world.entity.player.Player

class ContainerInteractionCloseEvent internal constructor(
    val interaction: ContainerInteraction,
    val player: Player
)
