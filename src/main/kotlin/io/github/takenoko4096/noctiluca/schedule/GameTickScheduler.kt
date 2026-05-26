package io.github.takenoko4096.noctiluca.schedule

import net.minecraft.server.MinecraftServer

class GameTickScheduler {
    private val tasks = mutableSetOf<GameTickTask>()

    private fun tick(server: MinecraftServer) {
        tasks.removeIf { task ->
            if (task.remaining <= 0) {
                task.callback(server)
                true
            }
            else {
                task.remaining--
                false
            }
        }
    }
}
