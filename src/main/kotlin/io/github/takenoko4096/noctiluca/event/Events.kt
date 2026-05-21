package io.github.takenoko4096.noctiluca.event

import kotlin.reflect.KClass

class Events internal constructor() {
    private val dispatchers = mutableSetOf<EventDispatcher<*>>()

    fun <E : Event> dispatcherOf(type: KClass<E>): EventDispatcher<E> {
        val matching = dispatchers.filter { it.type == type }
        return if (matching.isEmpty()) {
            EventDispatcher(type).apply {
                dispatchers.add(this)
            }
        }
        else if (matching.size == 1) {
            matching.first() as EventDispatcher<E>
        }
        else {
            throw IllegalStateException("NEVER HAPPENS")
        }
    }

    inline fun <reified E : Event> register(priority: HandlerPriority = HandlerPriority.NORMAL, noinline callback: E.() -> Unit): Int {
        return dispatcherOf(E::class).add(priority, callback)
    }

    inline fun <reified E : Event> unregister(id: Int) {
        dispatcherOf(E::class).remove(id)
    }
}
