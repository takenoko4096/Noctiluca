package io.github.takenoko4096.noctiluca.registry.block.templates

import io.github.takenoko4096.noctiluca.registry.block.ModBlockConfiguration
import net.minecraft.resources.Identifier

abstract class ModBlockTemplate {
    internal abstract fun getConfiguration(identifier: Identifier): ModBlockConfiguration.() -> Unit
}
