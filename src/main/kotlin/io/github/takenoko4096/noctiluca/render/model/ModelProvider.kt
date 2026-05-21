package io.github.takenoko4096.noctiluca.render.model

import net.minecraft.resources.ResourceKey

abstract class ModelProvider<T : Any>(protected val resourceKey: ResourceKey<T>)
