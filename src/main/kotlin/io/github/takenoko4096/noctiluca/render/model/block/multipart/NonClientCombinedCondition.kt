package io.github.takenoko4096.noctiluca.render.model.block.multipart

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.world.level.block.state.properties.Property

sealed class NonClientCombinedCondition(val terms: List<AbstractNonClientCondition>) : AbstractNonClientCondition() {
    class And(terms: List<AbstractNonClientCondition>) : NonClientCombinedCondition(terms)

    class Or(terms: List<AbstractNonClientCondition>) : NonClientCombinedCondition(terms)

    @NoctilucaDsl
    class Provider internal constructor() {
        fun <T : Comparable<T>> condition(property: Property<T>, vararg cases: T): NonClientSingleCondition<T> {
            return NonClientSingleCondition(property, cases as Array<T>, false)
        }

        fun and(vararg conditions: AbstractNonClientCondition): And {
            return And(conditions.toList())
        }

        fun or(vararg conditions: AbstractNonClientCondition): Or {
            return Or(conditions.toList())
        }
    }
}
