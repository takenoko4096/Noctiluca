package io.github.takenoko4096.noctiluca.schedule

data class GameTickTask<T>(internal val delay: Long, internal val execution: GameTickTask<T>.(T) -> Unit) {
    internal var remaining: Long = delay

    internal var isScheduled = false

    init {
        if (delay < 0L) {
            throw IllegalArgumentException("GameTickTaskの作成に失敗しました : 負の相対時刻に向けてタスクを作成することはできません")
        }
    }

    val isSchedulable
        get() = !isScheduled

    fun AbstractGameTickScheduler<T>.reschedule() {
        schedule(this@GameTickTask)
    }
}
