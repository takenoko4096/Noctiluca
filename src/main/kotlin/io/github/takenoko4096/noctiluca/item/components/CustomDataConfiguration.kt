package io.github.takenoko4096.noctiluca.item.components

import io.github.takenoko4096.mojangson.MojangsonSerializer
import io.github.takenoko4096.mojangson.values.MojangsonCompound
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.TagParser
import net.minecraft.world.item.component.CustomData

class CustomDataConfiguration internal constructor(mod: NoctilucaModInitializer, callback: CustomDataConfiguration.() -> Unit) : AbstractComponentConfiguration<CustomData>(mod, DataComponents.CUSTOM_DATA) {
    val compound = MojangsonCompound()

    init {
        callback()
    }

    override fun build(): CustomData {
        val tag = TagParser.parseCompoundFully(MojangsonSerializer.structure(compound, false))
        return CustomData.of(tag)
    }
}
