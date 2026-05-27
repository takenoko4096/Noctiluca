package io.github.takenoko4096.noctiluca.schedule

import io.github.takenoko4096.noctiluca.Noctiluca
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.MinecraftServer
import kotlin.collections.component1
import kotlin.collections.component2

class GameTickScheduler(private val useServerTickRate: Boolean = true) {
    private var maximum = Int.MIN_VALUE

    private val tasks = mutableMapOf<Int, GameTickTask>()

    private fun tick(handle: MutableIterator<GameTickScheduler>, server: MinecraftServer) {
        val iterator = tasks.iterator()

        while (iterator.hasNext()) {
            val (_, task) = iterator.next()

            if (task.remaining > 0) task.remaining--
            else {
                iterator.remove()
                task.isScheduled = false
                task.remaining = task.delay
                try {
                    task.execution(task, server)
                }
                catch (error: Throwable) {
                    Noctiluca.logger.warn("An exception was thrown from GameTickTask: ", error)
                }
            }
        }

        if (tasks.isEmpty()) {
            handle.remove()
        }
    }

    fun schedule(task: GameTickTask): Int {
        if (task.isScheduled) {
            throw IllegalStateException("Already scheduled! Please use copy()")
        }

        val id = maximum++
        tasks[id] = task
        task.isScheduled = true

        if (this !in schedulers) {
            schedulers.add(this)
        }

        return id
    }

    fun timeout(delay: Long, callback: GameTickTask.(MinecraftServer) -> Unit): Int {
        return schedule(GameTickTask(delay, callback))
    }

    fun interval(interval: Long, callback: GameTickTask.(MinecraftServer) -> Unit) {
        GameTickTask(interval) {
            callback(it)
        }
    }

    fun cancel(id: Int): GameTickTask {
        return tasks.remove(id) ?: throw IllegalArgumentException("Invalid task id")
    }

    fun clear() {
        tasks.clear()
    }

    companion object {
        private val schedulers = mutableSetOf<GameTickScheduler>()

        init {
            ServerTickEvents.END_SERVER_TICK.register(::onServerTick)
        }

        private fun onServerTick(server: MinecraftServer) {
            val iterator = schedulers.iterator()

            while (iterator.hasNext()) {
                val scheduler = iterator.next()

                if (scheduler.useServerTickRate) {
                    scheduler.tick(iterator, server)
                }
            }
        }

        private fun onProgramTick(server: MinecraftServer) {

        }
    }
}
