package io.github.takenoko4096.noctiluca.portal

import io.github.takenoko4096.noctiluca.text.ArgbColor
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.Objects

class PortalType private constructor(val identifier: Identifier, val frameBlock: Block, val tintColor: ArgbColor, val ignitionSource: Item, val maxWidth: Int, val maxHeight: Int) {
    val portalFinder: PortalFinder = PortalFinder(this)

    val id: Int
        get() {
            return types.entries.find { it.value == this }?.key?: throw IllegalArgumentException("unregistered?")
        }

    override fun hashCode(): Int {
        return Objects.hash(identifier)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PortalType) return false
        return identifier == other.identifier
    }

    companion object {
        private var types = mutableMapOf<Int, PortalType>()

        fun register(identifier: Identifier, frameBlock: Block, tintColor: ArgbColor, ignitionSource: Item, maxWidth: Int = 21, maxHeight: Int = 21) {
            if (types.size >= 1024) {
                throw IllegalArgumentException("ポータルタイプ数が最大に到達しました: ${types.size}")
            }

            if (types.values.any { it.identifier == identifier }) {
                throw IllegalArgumentException("ポータルタイプIDが重複しています: $identifier")
            }

            if (types.values.any { it.frameBlock == frameBlock }) {
                throw IllegalArgumentException("ポータルフレームに使用できないブロックです: 既に使用されています")
            }

            types[1024] = PortalType(identifier, frameBlock, tintColor, ignitionSource, maxWidth, maxHeight)

            val new = mutableMapOf<Int, PortalType>()
            for ((id, type) in types.values.sortedBy { it.identifier }.withIndex()) {
                new[id] = type
            }
            types = new
        }

        fun getByFrame(frameBlock: Block): PortalType? {
            return types.values.find { it.frameBlock == frameBlock }
        }

        fun getById(id: Int): PortalType? {
            return types[id]
        }
    }
}
