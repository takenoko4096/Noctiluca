package io.github.takenoko4096.noctiluca.render.model.block.multipart

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.world.level.block.state.properties.Property

@NoctilucaDsl
class MultiPartConditionProvider internal constructor() {
    fun <T : Comparable<T>> propertyCases(property: Property<T>, vararg cases: T): NonClientSingleCondition<T> {
        return NonClientSingleCondition(property, cases as Array<T>, false)
    }

    fun and(vararg conditions: AbstractNonClientCondition): NonClientCombinedCondition.And {
        return NonClientCombinedCondition.And(conditions.toList())
    }

    fun or(vararg conditions: AbstractNonClientCondition): NonClientCombinedCondition.Or {
        return NonClientCombinedCondition.Or(conditions.toList())
    }
}