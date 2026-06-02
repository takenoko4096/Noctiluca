package io.github.takenoko4096.noctiluca.portal

import net.minecraft.world.level.block.Block

data class PortalType(
    val frameBlock: Block,
    val portalBlock: Block,
    val maxWidth: Int,
    val maxHeight: Int
)
