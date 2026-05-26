package io.github.takenoko4096.noctiluca.schedule

import net.minecraft.server.MinecraftServer

data class GameTickTask(
    val id: Int,
    var remaining: Int,
    val callback: MinecraftServer.() -> Unit
)
