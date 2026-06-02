package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.math.Position3i
import io.github.takenoko4096.noctiluca.text.ArgbColor
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockAndLightGetter
import net.minecraft.world.level.block.state.BlockState

@NoctilucaDsl
class TintConfiguration internal constructor(callback: TintConfiguration.() -> Unit) {
    internal var default: (BlockState) -> Int = { -1 }

    internal var inWorld: ((BlockState, BlockPos, BlockAndLightGetter) -> Int)? = null

    internal var particle: ((BlockState, BlockPos, BlockAndLightGetter) -> Int)? = null

    init {
        callback()
    }

    fun default(callback: (BlockState) -> ArgbColor) {
        default = { callback(it).argbValue }
    }

    fun inWorld(callback: TintProvider.() -> ArgbColor) {
        inWorld = { blockState, blockPos, blockAndLightGetter -> TintProvider(blockState, Position3i.from(blockPos), blockAndLightGetter, callback).color.argbValue }
    }

    fun particle(callback: TintProvider.() -> ArgbColor) {
        particle = { blockState, blockPos, blockAndLightGetter -> TintProvider(blockState, Position3i.from(blockPos), blockAndLightGetter, callback).color.argbValue }
    }

    class TintProvider internal constructor(val blockState: BlockState, val position: Position3i, val blockAndLightGetter: BlockAndLightGetter, callback: TintProvider.() -> ArgbColor) {
        internal var color: ArgbColor = callback()
    }
}
