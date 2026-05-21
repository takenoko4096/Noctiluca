package io.github.takenoko4096.noctiluca.registry.tag

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey

class ModTagConfiguration<T : Any> internal constructor(internal val target: ResourceKey<Registry<T>>, val key: TagKey<T>, callback: ModTagConfiguration<T>.() -> Unit) {
    private val entries = mutableSetOf<T>()

    var replace: Boolean = false

    init {
        callback()
    }

    fun entry(t: T) {
        entries.add(t)
    }

    internal fun build(): Tag<T> {
        return Tag(target, key, entries.toSet(), replace)
    }
}
