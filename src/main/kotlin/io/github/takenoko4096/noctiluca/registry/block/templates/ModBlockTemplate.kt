package io.github.takenoko4096.noctiluca.registry.block.templates

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.math.Position3i
import io.github.takenoko4096.noctiluca.registry.block.BlockPropertiesConfiguration
import io.github.takenoko4096.noctiluca.registry.block.ModBlockConfiguration
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor

abstract class ModBlockTemplate<T : Block> {
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

    @NoctilucaDsl
    class AmbientConfiguration internal constructor(callback: AmbientConfiguration.() -> Unit) {
        internal var sound: SoundConfiguration = SoundConfiguration {}

        var particle: ParticleOptions? = null

        init {
            callback()
        }

        fun sound(callback: SoundConfiguration.() -> Unit) {
            sound = SoundConfiguration(callback)
        }

        @NoctilucaDsl
        class SoundConfiguration internal constructor(callback: SoundConfiguration.() -> Unit) {
            var soundEvent: SoundEvent = SoundEvents.EMPTY

            internal var volume: (SoundValueProvider.() -> Float)? = null

            internal var pitch: (SoundValueProvider.() -> Float)? = null

            init {
                callback()
            }

            fun volume(callback: SoundValueProvider.() -> Float) {
                volume = callback
            }

            fun pitch(callback: SoundValueProvider.() -> Float) {
                pitch = callback
            }
        }

        @NoctilucaDsl
        class SoundValueProvider internal constructor(val level: Level, val position: Position3i, val randomSource: RandomSource, callback: SoundValueProvider.() -> Float) {
            internal val value = callback()
        }
    }
}
