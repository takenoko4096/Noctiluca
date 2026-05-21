package io.github.takenoko4096.noctiluca.registry.tag

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey

class Tag<T : Any> internal constructor(
    val target: ResourceKey<Registry<T>>,
    val tag: TagKey<T>,
    val entries: Set<T>,
    val replace: Boolean
)
