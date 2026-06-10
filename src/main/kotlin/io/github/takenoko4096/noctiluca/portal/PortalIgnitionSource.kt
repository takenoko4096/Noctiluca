package io.github.takenoko4096.noctiluca.portal

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.BaseFireBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FireBlock
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.SoulFireBlock
import net.minecraft.world.level.material.Fluids
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
        internal fun block(block: Block): SourceBlock = SourceBlock(block)

        fun <T : BaseFireBlock> fire(fire: T): SourceBlock = block(fire)

        fun <T : LiquidBlock> liquid(liquid: LiquidBlock) = block(liquid)

        val WATER = block(Blocks.WATER)

        val LAVA = block(Blocks.LAVA)

        val FIRE = block(Blocks.FIRE)

        val SOUL_FIRE = block(Blocks.SOUL_FIRE)

        fun item(item: Item): SourceItem = SourceItem(item)
    }
}
