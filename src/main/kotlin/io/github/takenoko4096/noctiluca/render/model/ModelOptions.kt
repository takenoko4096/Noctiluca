package io.github.takenoko4096.noctiluca.render.model

import io.github.takenoko4096.noctiluca.NoctilucaDsl

@NoctilucaDsl
class ModelOptions internal constructor(callback: ModelOptions.() -> Unit) {
    var suffix: String? = null

    init {
        callback()
    }
}
