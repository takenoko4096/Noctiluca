package io.github.takenoko4096.noctiluca.registry.block.templates

import io.github.takenoko4096.noctiluca.registry.block.BlockPropertiesConfiguration
import io.github.takenoko4096.noctiluca.registry.block.ModBlockConfiguration
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor

abstract class ModBlockTemplate {
    private var mapColor: ((BlockState) -> MapColor)? = null

    fun mapColor(callback: (BlockState) -> MapColor) {
        mapColor = callback
    }

    protected fun BlockPropertiesConfiguration.setMapColor() {
        if (mapColor != null) {
            mapColor(mapColor!!)
        }
    }

    abstract fun getConfigurator(identifier: Identifier): ModBlockConfiguration.() -> Unit
}
