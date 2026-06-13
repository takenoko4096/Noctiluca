package io.github.takenoko4096.noctiluca.registry.block.templates

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.math.Position3i
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level

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