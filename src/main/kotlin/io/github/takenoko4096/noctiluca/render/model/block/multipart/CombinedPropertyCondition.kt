package io.github.takenoko4096.noctiluca.render.model.block.multipart

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.world.level.block.state.properties.Property

sealed class CombinedPropertyCondition(val terms: List<ICondition>) : ICondition {
    class And(terms: List<ICondition>) : CombinedPropertyCondition(terms)

    class Or(terms: List<ICondition>) : CombinedPropertyCondition(terms)

    @NoctilucaDsl
    class Provider internal constructor() {
        fun <T : Comparable<T>> condition(property: Property<T>, vararg cases: T): ConditionTerm<T> {
            return ConditionTerm(property, cases as Array<T>, false)
        }

        fun and(vararg conditions: ICondition): And {
            return And(conditions.toList())
        }

        fun or(vararg conditions: ICondition): Or {
            return Or(conditions.toList())
        }
    }
}
