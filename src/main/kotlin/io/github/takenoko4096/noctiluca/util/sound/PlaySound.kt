package io.github.takenoko4096.noctiluca.util.sound

import net.minecraft.core.Holder
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import kotlin.math.sqrt

object PlaySound {
    fun playSound(players: Set<ServerPlayer>, sound: SoundEvent, position: Vec3, volume: Float, pitch: Float, minVolume: Float = 1f) {
        val maxDistSqr = Mth.square(sound.getRange(volume)).toDouble()

        for (player in players) {
            val seed = player.level().getRandom().nextLong()
            val deltaX = position.x - player.x
            val deltaY = position.y - player.y
            val deltaZ = position.z - player.z
            val distSqr = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ
            var localPosition = position
            var localVolume = volume
            if (distSqr > maxDistSqr) {
                if (minVolume <= 0.0f) {
                    continue
                }

                val distance = sqrt(distSqr)
                localPosition = Vec3(
                    player.x + deltaX / distance * 2.0,
                    player.y + deltaY / distance * 2.0,
                    player.z + deltaZ / distance * 2.0
                )
                localVolume = minVolume
            }

            player.connection.send(
                ClientboundSoundPacket(
                    Holder.direct(sound),
                    player.soundSource,
                    localPosition.x(),
                    localPosition.y(),
                    localPosition.z(),
                    localVolume,
                    pitch,
                    seed
                )
            )
        }
    }

    fun playSound(player: ServerPlayer, sound: SoundEvent, volume: Float, pitch: Float, minVolume: Float = 1f) {
        playSound(setOf(player), sound, player.position(), volume, pitch, minVolume)
    }
}
