package io.github.takenoko4096.noctiluca.render

import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

open class TexturePath internal constructor(val identifier: Identifier) {
    infix fun underscore(suffix: String): TexturePath {
        return TexturePath(identifier.withSuffix("_$suffix"))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TexturePath) return false

        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }

    companion object {
        internal fun blockDefault(resourceKey: ResourceKey<Block>): TexturePath {
            return TexturePath(resourceKey.identifier().withPrefix("block/"))
        }

        internal fun itemDefault(resourceKey: ResourceKey<Item>): TexturePath {
            return TexturePath(resourceKey.identifier().withPrefix("item/"))
        }
    }
}
