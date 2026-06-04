package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.Noctiluca
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.math.Position3i
import io.github.takenoko4096.noctiluca.math.Vector3d
import io.github.takenoko4096.noctiluca.portal.PortalAxis
import io.github.takenoko4096.noctiluca.portal.PortalType
import io.github.takenoko4096.noctiluca.registry.StarlightRegistry
import io.github.takenoko4096.noctiluca.registry.block.templates.ModBlockTemplate
import io.github.takenoko4096.noctiluca.render.TexturePath
import io.github.takenoko4096.noctiluca.render.model.block.NonClientVariantMutator
import io.github.takenoko4096.noctiluca.text.ArgbColor
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.PushReaction
import kotlin.math.min

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

    fun registerUsingTemplate(identifier: String, template: ModBlockTemplate): CustomPortalBlock {
        return register(identifier, template.getConfiguration(mod.identifierOf(identifier))) as CustomPortalBlock
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
