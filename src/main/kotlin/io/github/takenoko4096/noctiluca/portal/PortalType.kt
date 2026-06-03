package io.github.takenoko4096.noctiluca.portal

import io.github.takenoko4096.noctiluca.text.ArgbColor
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.Objects

class PortalType private constructor(val id: Int, val frameBlock: Block, val tintColor: ArgbColor, val ignitionSource: Item, val maxWidth: Int, val maxHeight: Int) {
    val portalFinder: PortalFinder = PortalFinder(this)

    override fun hashCode(): Int {
        return Objects.hash(frameBlock)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PortalType) return false
        return frameBlock == other.frameBlock
    }

    companion object {
        private val types = mutableMapOf<Int, PortalType>()

        private var maximum = 0

        fun register(frameBlock: Block, tintColor: ArgbColor, ignitionSource: Item, maxWidth: Int = 21, maxHeight: Int = 21): PortalType {
            if (types.values.any { it.frameBlock == frameBlock }) {
                throw IllegalArgumentException("ポータルフレームに使用できないブロックです: 既に使用されています")
            }
            val id = maximum++
            val type = PortalType(id, frameBlock, tintColor, ignitionSource, maxWidth, maxHeight)
            types[id] = type
            return type
        }

        fun getAllTypes(): Set<PortalType> {
            return types.values.toSet()
        }

        fun getById(id: Int): PortalType? {
            return types[id]
        }
    }
}
