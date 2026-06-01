package io.github.takenoko4096.noctiluca.render.model.block

import io.github.takenoko4096.noctiluca.render.model.ModelOptions
import io.github.takenoko4096.noctiluca.render.model.NonClientModel
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey

class NonClientFileModel(
    resourceKey: ResourceKey<*>,
    val location: Identifier,
    options: ModelOptions
) : NonClientModel(resourceKey, options)
