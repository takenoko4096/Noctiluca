package io.github.takenoko4096.noctiluca.registry.item.templates

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.registry.item.CustomItem
import io.github.takenoko4096.noctiluca.registry.item.ModItemConfiguration
import net.minecraft.resources.Identifier

@NoctilucaDsl
abstract class ModItemTemplate<T : CustomItem> {
    abstract fun getConfigurator(identifier: Identifier): ModItemConfiguration.() -> Unit
}
