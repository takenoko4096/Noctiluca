package io.github.takenoko4096.noctiluca.portal

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.Objects

sealed class PortalIgnitionSource<T : Any>(val source: T) {
    class SourceItem internal constructor(item: Item) : PortalIgnitionSource<Item>(item)
    class SourceBlock internal constructor(block: Block) : PortalIgnitionSource<Block>(block)

    override fun hashCode(): Int {
        return Objects.hash(this::class, source)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PortalIgnitionSource<*>) return false
        if (other::class != this::class) return false
        return source == other.source
    }

    companion object {
        fun block(block: Block): SourceBlock = SourceBlock(block)

        fun item(item: Item): SourceItem = SourceItem(item)
    }
}
