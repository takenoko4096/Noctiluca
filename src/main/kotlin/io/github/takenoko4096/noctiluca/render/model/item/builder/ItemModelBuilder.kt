package io.github.takenoko4096.noctiluca.render.model.item.builder

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.render.model.NonClientModel
import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.Condition
import io.github.takenoko4096.noctiluca.render.model.item.builder.rangedispatch.RangeDispatch
import io.github.takenoko4096.noctiluca.render.model.item.builder.rangedispatch.RangeDispatchEntry
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.Select
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.SelectCase

@NoctilucaDsl
class ItemModelBuilder internal constructor(callback: ItemModelBuilder.() -> Unit) {
    internal var handle: ItemModelHandle? = null

    init {
        callback()
    }

    fun <T : Select<C>, C> select(select: T, callback: T.() -> Unit) {
        select.callback()
        handle = select
    }

    fun <T : Condition<C>, C> condition(condition: T, callback: T.() -> Unit) {
        condition.callback()
        handle = condition
    }

    fun <T : RangeDispatch<C>, C> rangeDispatch(rangeDispatch: T, callback: T.() -> Unit) {
        rangeDispatch.callback()
        handle = rangeDispatch
    }

    fun use(model: NonClientModel) {
        handle = End(model)
    }

    fun build(): ItemModelHandle {
        return handle ?: throw IllegalStateException("empty item model handle")
    }

    object AccessForClient {
        fun <C> getSelectCases(select: Select<C>): List<SelectCase<C>> {
            return select.cases.cases.toList()
        }

        fun <C> getSelectFallback(select: Select<C>): ItemModelHandle {
            return select.fallback.build()
        }

        fun <C> getConditionOnTrue(condition: Condition<C>): ItemModelHandle {
            return condition.onTrueModel.build()
        }

        fun <C> getConditionOnFalse(condition: Condition<C>): ItemModelHandle {
            return condition.onFalseModel.build()
        }

        fun <C> getRangeDispatchEntries(rangeDispatch: RangeDispatch<C>): List<RangeDispatchEntry<C>> {
            return rangeDispatch.entries.entries.toList()
        }

        fun <C> getRangeDispatchFallback(rangeDispatch: RangeDispatch<C>): ItemModelHandle {
            return rangeDispatch.fallback.build()
        }
    }
}
