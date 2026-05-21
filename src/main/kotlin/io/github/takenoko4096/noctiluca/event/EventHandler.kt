package io.github.takenoko4096.noctiluca.event

class EventHandler<E : Event> internal constructor(
    internal val id: Int,
    internal val callback: E.() -> Unit
)
