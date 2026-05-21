package io.github.takenoko4096.noctiluca.event

enum class HandlerPriority(val priority: Int) {
    EARLIEST(5),
    EARLIER(4),
    NORMAL(3),
    SLOWER(2),
    SLOWEST(1)
}
