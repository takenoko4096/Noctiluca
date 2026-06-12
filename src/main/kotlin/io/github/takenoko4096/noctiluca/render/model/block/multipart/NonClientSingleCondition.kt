package io.github.takenoko4096.noctiluca.render.model.block.multipart

import net.minecraft.world.level.block.state.properties.Property

class NonClientSingleCondition<T : Comparable<T>>(val property: Property<T>, val cases: Array<T>, val not: Boolean) : AbstractNonClientCondition() {
    val firstExclusive: Array<T> = cases.copyOfRange(1, cases.size)

    init {
        if (cases.isEmpty()) {
            throw IllegalArgumentException("ConditionTerm.casesには1つ以上の値を使用する必要があります")
        }
    }

    fun not(): NonClientSingleCondition<T> {
        return NonClientSingleCondition(property, cases, !not)
    }
}
