package io.github.takenoko4096.noctiluca.nbt

import io.github.takenoko4096.mojangson.MojangsonParser
import io.github.takenoko4096.mojangson.MojangsonSerializer
import io.github.takenoko4096.mojangson.values.MojangsonCompound
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.TagParser

fun CompoundTag.toMojangsonCompound(): MojangsonCompound {
    return MojangsonParser.compound(toString())
}

fun MojangsonCompound.toCompoundTag(): CompoundTag {
    return TagParser.parseCompoundFully(MojangsonSerializer.structure(this))
}
