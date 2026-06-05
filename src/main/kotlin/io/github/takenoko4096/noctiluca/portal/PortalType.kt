package io.github.takenoko4096.noctiluca.portal

import io.github.takenoko4096.noctiluca.registry.block.CustomPortalBlock
import io.github.takenoko4096.noctiluca.text.ArgbColor
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import java.util.Objects
import kotlin.math.min

class PortalType private constructor(val identifier: Identifier, val frameBlock: Block, val portalBlock: CustomPortalBlock, val ignitionSource: Item, val dimension1: ResourceKey<Level>, val dimension2: ResourceKey<Level>, val maxWidth: Int, val maxHeight: Int) {
    val portalFinder: PortalFinder = PortalFinder(this)

    override fun hashCode(): Int {
        return Objects.hash(identifier)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PortalType) return false
        return identifier == other.identifier
    }

    companion object {
        private var types = mutableMapOf<Identifier, PortalType>()

        fun register(identifier: Identifier, frameBlock: Block, portalBlock: CustomPortalBlock, ignitionSource: Item, dimension1: ResourceKey<Level>, dimension2: ResourceKey<Level>, maxWidth: Int = 21, maxHeight: Int = 21) {
            if (identifier in types) {
                throw IllegalArgumentException("IDが重複しています: $identifier")
            }

            if (types.values.any { it.identifier == identifier }) {
                throw IllegalArgumentException("ポータルタイプIDが重複しています: $identifier")
            }

            if (types.values.any { it.frameBlock == frameBlock }) {
                throw IllegalArgumentException("ポータルフレームに使用できないブロックです: 既に使用されています")
            }

            if (types.values.any { it.portalBlock == portalBlock }) {
                throw IllegalArgumentException("ポータルブロックに使用できないブロックです: 既に使用されています")
            }

            types[identifier] = PortalType(identifier, frameBlock, portalBlock, ignitionSource, dimension1, dimension2, maxWidth, maxHeight)
        }

        fun get(identifier: Identifier): PortalType? {
            return types[identifier]
        }

        fun getByFrameBlock(frameBlock: Block): PortalType? {
            return types.values.find { it.frameBlock == frameBlock }
        }

        fun getByPortalBlock(block: Block): PortalType? {
            return types.values.find { it.portalBlock == block }
        }
    }
}
