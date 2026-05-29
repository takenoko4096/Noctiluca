package io.github.takenoko4096.noctiluca.execute

import io.github.takenoko4096.noctiluca.math.Rotation2f
import io.github.takenoko4096.noctiluca.math.Vector3d
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.Level

abstract class NoctilucaCommandSender {
    abstract fun getPosition(): Vector3d

    abstract fun getRotation(): Rotation2f

    abstract fun getDimension(): Level

    abstract fun getServer(): MinecraftServer
}
