package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.registry.StarlightRegistry
import io.github.takenoko4096.noctiluca.registry.block.templates.ModBlockTemplate
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block

class ModBlockRegistry(mod: NoctilucaModInitializer) : StarlightRegistry(mod) {
    private val configurations = mutableSetOf<ModBlockConfiguration>()

    internal val blocks = mutableMapOf<ResourceKey<Block>, Block>()

    private val properties = mutableMapOf<Block, Properties>()

    fun register(identifier: String, callback: ModBlockConfiguration.() -> Unit): Block {
        val configuration = ModBlockConfiguration(this, identifier, callback)
        val block = configuration.register()
        configurations.add(configuration)
        properties[block] = Properties(configuration.propertyDefinitions.toSet())
        blocks[configuration.blockResourceKey] = block
        return block
    }

    inline fun <reified T : Block> registerUsingTemplate(identifier: String, template: ModBlockTemplate<T>): T {
        return register(identifier, template.getConfigurator(mod.identifierOf(identifier))) as T
    }

    fun getPropertiesOf(block: Block): Properties {
        return properties[block] ?: throw IllegalArgumentException("properties not found")
    }

    fun getBlock(resourceKey: ResourceKey<Block>): Block {
        return blocks[resourceKey] ?: throw IllegalArgumentException("ブロック '${resourceKey.identifier()}' が Modブロックレジストリに見つかりませんでした: ${blocks.keys.map { it.identifier() }}")
    }

    fun getConfigurations(): Set<ModBlockConfiguration> {
        return configurations.toSet()
    }
}
