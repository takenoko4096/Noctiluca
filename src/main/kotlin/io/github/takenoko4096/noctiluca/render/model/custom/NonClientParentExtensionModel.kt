package io.github.takenoko4096.noctiluca.render.model.custom

import io.github.takenoko4096.noctiluca.render.TexturePath
import io.github.takenoko4096.noctiluca.render.model.ModelOptions
import io.github.takenoko4096.noctiluca.render.model.NonClientModel
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey

class NonClientParentExtensionModel(
    resourceKey: ResourceKey<*>,
    val parent: Identifier,
    val mapping: Map<String, TexturePath>,
    options: ModelOptions
) : NonClientModel(resourceKey, options)
