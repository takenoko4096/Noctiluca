package io.github.takenoko4096.noctiluca.event

import kotlin.reflect.KClass

class EventDispatcher< E : Event>(val type: KClass<E>) {
    private var maxId = 0

    private val handlerMap = mutableMapOf<HandlerPriority, MutableSet<EventHandler<E>>>()

    override fun hashCode(): Int {
        return type.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EventDispatcher<*>) return false
        return type == other.type
    }

    fun dispatch(event: E, callAfterCancelled: Boolean = false) {
        for ((_, handlerSet) in handlerMap.entries.sortedByDescending { it.key.priority }) {
            for (handler in handlerSet) {
                handler.callback(event)
                if (event is CancellableEvent && event.isCancelled && !callAfterCancelled) break
            }
        }
    }

    fun add(priority: HandlerPriority, callback: E.() -> Unit): Int {
        val handlerSet = handlerMap[priority] ?: mutableSetOf()
        val id = maxId++
        handlerSet.add(EventHandler(id, callback))
        return id
    }

    fun remove(id: Int) {
        for (handlerSet in handlerMap.values) {
            for (handler in handlerSet) {
                if (handler.id == id) {
                    handlerSet.remove(handler)
                    break
                }
            }
        }
    }

    fun clear() {
        handlerMap.clear()
    }
}
