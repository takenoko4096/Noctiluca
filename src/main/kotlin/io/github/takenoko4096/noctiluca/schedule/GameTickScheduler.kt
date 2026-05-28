package io.github.takenoko4096.noctiluca.schedule

import io.github.takenoko4096.noctiluca.Noctiluca
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.mixin.event.lifecycle.MinecraftServerMixin
import net.minecraft.server.MinecraftServer
import kotlin.collections.component1
import kotlin.collections.component2

class GameTickScheduler(private val respectTickRate: Boolean = true) {
    private var maximum = Int.MIN_VALUE

    private val tasks = mutableMapOf<Int, GameTickTask>()

    private fun tick(server: MinecraftServer) {
        val iterator = tasks.toMap().iterator()

        while (iterator.hasNext()) {
            val (id, task) = iterator.next()

            if (task.remaining > 0) task.remaining--
            else {
                tasks.remove(id)
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
            schedulers.remove(this)
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

    fun timeout(delay: Long = 0L, callback: GameTickTask.(MinecraftServer) -> Unit): Int {
        return schedule(GameTickTask(delay, callback))
    }

    fun interval(interval: Long= 0L, callback: GameTickTask.(MinecraftServer) -> Unit) {
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
            ServerTickEvents.END_SERVER_TICK.register {
                tick(it, respectTickRate = false)
            }

            // each?
            ServerTickEvents.END_LEVEL_TICK.register {
                tick(it.server!!, respectTickRate = true)
            }
        }

        private fun tick(server: MinecraftServer, respectTickRate: Boolean) {
            val iterator = schedulers.toSet().iterator()

            while (iterator.hasNext()) {
                val scheduler = iterator.next()

                if (scheduler.respectTickRate == respectTickRate) {
                    scheduler.tick(server)
                }
            }
        }
    }
}
