package io.github.takenoko4096.noctiluca.render.model

import io.github.takenoko4096.noctiluca.render.model.block.NonClientBlockModelVariant
import io.github.takenoko4096.noctiluca.render.model.block.NonClientVariantMutator
import net.minecraft.resources.ResourceKey

abstract class NonClientModel(private val resourceKey: ResourceKey<*>, val options: ModelOptions) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NonClientModel) return false

        if (resourceKey != other.resourceKey) return false
        if (options.suffix != other.options.suffix) return false

        return true
    }

    override fun hashCode(): Int {
        var result = resourceKey.hashCode()
        result = 31 * result + options.suffix.hashCode()
        return result
    }

    fun toVariant(vararg mutators: NonClientVariantMutator): NonClientBlockModelVariant {
        return NonClientBlockModelVariant(
            this,
            mutators.toList()
        )
    }
}
