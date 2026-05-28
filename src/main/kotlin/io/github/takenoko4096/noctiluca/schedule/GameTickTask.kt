package io.github.takenoko4096.noctiluca.schedule

import net.minecraft.server.MinecraftServer

data class GameTickTask(internal val delay: Long, internal val execution: GameTickTask.(MinecraftServer) -> Unit) {
    internal var remaining: Long = delay

    internal var isScheduled = false

    init {
        if (delay < 0L) {
            throw IllegalArgumentException("GameTickTaskの作成に失敗しました : 負の相対時刻に向けてタスクを作成することはできません")
        }
    }

    val isSchedulable
        get() = !isScheduled

    fun GameTickScheduler.reschedule() {
        schedule(this@GameTickTask)
    }
}
