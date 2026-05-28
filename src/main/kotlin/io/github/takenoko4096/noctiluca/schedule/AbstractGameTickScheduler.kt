package io.github.takenoko4096.noctiluca.schedule

import io.github.takenoko4096.noctiluca.Noctiluca
import kotlin.collections.component1
import kotlin.collections.component2

abstract class AbstractGameTickScheduler<T> protected constructor(private val ticker: SchedulerTicker<T>) {
    private var maximum = Int.MIN_VALUE

    private val tasks = mutableMapOf<Int, GameTickTask<T>>()

    private fun tick(server: T) {
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
            ticker.unsubscribe(this)
        }
    }

    fun schedule(task: GameTickTask<T>): Int {
        if (task.isScheduled) {
            throw IllegalStateException("Already scheduled! Please use copy()")
        }

        val id = maximum++
        tasks[id] = task
        task.isScheduled = true

        ticker.subscribe(this)

        return id
    }

    fun timeout(delay: Long = 0L, callback: GameTickTask<T>.(T) -> Unit): Int {
        return schedule(GameTickTask(delay, callback))
    }

    fun interval(interval: Long= 0L, callback: GameTickTask<T>.(T) -> Unit) {
        GameTickTask(interval) {
            callback(it)
        }
    }

    fun cancel(id: Int): GameTickTask<T> {
        return tasks.remove(id) ?: throw IllegalArgumentException("Invalid task id")
    }

    fun clear() {
        tasks.clear()
    }

    abstract class SchedulerTicker<T> protected constructor() {
        private val schedulers = mutableSetOf<AbstractGameTickScheduler<T>>()

        internal fun subscribe(scheduler: AbstractGameTickScheduler<T>) {
            if (scheduler !in schedulers) {
                schedulers.add(scheduler)
            }
        }

        internal fun unsubscribe(scheduler: AbstractGameTickScheduler<T>) {
            schedulers.remove(scheduler)
        }

        protected fun tick(parameter: T) {
            val iterator = schedulers.toSet().iterator()

            while (iterator.hasNext()) {
                val scheduler = iterator.next()

                scheduler.tick(parameter)
            }
        }
    }
}
