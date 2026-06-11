package io.github.takenoko4096.noctiluca.render.model.block.multipart

import net.minecraft.world.level.block.state.properties.Property

class ConditionTerm<T : Comparable<T>>(val property: Property<T>, val cases: Set<T>, val not: Boolean) {
    init {
        if (cases.isEmpty()) {
            throw IllegalArgumentException("ConditionTerm.casesには1つ以上の値を使用する必要があります")
        }
    }
}
