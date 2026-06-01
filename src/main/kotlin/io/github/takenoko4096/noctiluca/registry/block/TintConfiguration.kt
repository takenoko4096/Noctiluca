package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockAndLightGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

@NoctilucaDsl
class TintConfiguration internal constructor(callback: TintConfiguration.() -> Unit) {
    internal var defaultColorGetter: (BlockState) -> Int = { -1 }

    internal var colorGetter: (BlockState, BlockPos, BlockAndLightGetter) -> Int = { a, b, c -> -1 }

    internal var particleColorGetter: (BlockState, BlockPos, BlockAndLightGetter) -> Int = { a, b, c -> -1 }

    init {
        callback()
    }

    fun default(callback: (BlockState) -> Int) {
        defaultColorGetter = callback
    }

    fun inWorld(callback: (BlockState, BlockPos, BlockAndLightGetter) -> Int) {
        colorGetter = callback
    }

    fun terrainParticle(callback: (BlockState, BlockPos, BlockAndLightGetter) -> Int) {
        particleColorGetter = callback
    }
}