package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState

@NoctilucaDsl
class BlockRotatableConfiguration internal constructor(val blockState: BlockState, val rotation: Rotation, callback: BlockRotatableConfiguration.() -> Unit) {
    var finalBlockState: BlockState = blockState

    init {
        callback()
    }
}

