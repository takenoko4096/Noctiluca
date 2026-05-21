package io.github.takenoko4096.noctiluca.event

abstract class CancellableEvent protected constructor() : Event() {
    var isCancelled: Boolean = false
}

