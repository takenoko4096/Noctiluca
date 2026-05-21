package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.registry.StarlightRegistry
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block

class ModBlockRegistry(mod: NoctilucaModInitializer) : StarlightRegistry(mod) {
    private val configurations = mutableSetOf<ModBlockConfiguration>()

    internal val blocks = mutableMapOf<ResourceKey<Block>, Block>()

    fun register(identifier: String, configuration: ModBlockConfiguration.() -> Unit): Block {
        val o = ModBlockConfiguration(this, identifier)
        o.configuration()
        val block = o.register()
        configurations.add(o)
        blocks[o.blockResourceKey] = block
        return block
    }

    fun getBlock(resourceKey: ResourceKey<Block>): Block {
        return blocks[resourceKey] ?: throw IllegalArgumentException("ブロック '${resourceKey.identifier()}' が Modブロックレジストリに見つかりませんでした: ${blocks.keys.map { it.identifier() }}")
    }

    fun getConfigurations(): Set<ModBlockConfiguration> {
        return configurations.toSet()
    }
}
