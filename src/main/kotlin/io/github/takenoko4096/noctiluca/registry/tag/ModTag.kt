package io.github.takenoko4096.noctiluca.registry.tag

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey

class ModTag<T : Any> internal constructor(
    val target: ResourceKey<Registry<T>>,
    val tagKey: TagKey<T>,
    val entries: Set<ResourceKey<T>>,
    val replace: Boolean
)
