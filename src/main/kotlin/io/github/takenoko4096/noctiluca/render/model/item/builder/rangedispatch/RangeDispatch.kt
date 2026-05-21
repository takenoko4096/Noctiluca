package io.github.takenoko4096.noctiluca.render.model.item.builder.rangedispatch

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.render.model.item.builder.ItemModelBuilder
import io.github.takenoko4096.noctiluca.render.model.item.builder.ItemModelHandle

class RangeDispatch<C> : ItemModelHandle() {
    internal var entries: RangeDispatchEntries<C> = RangeDispatchEntries {}

    var scale: Float = 1f

    internal var fallback: ItemModelBuilder = ItemModelBuilder {}

    fun entries(callback: RangeDispatchEntries<C>.() -> Unit) {
        entries = RangeDispatchEntries(callback)
    }

    fun fallback(callback: ItemModelBuilder.() -> Unit) {
        fallback = ItemModelBuilder(callback)
    }

    @NoctilucaDsl
    class RangeDispatchEntries<C>(callback: RangeDispatchEntries<C>.() -> Unit) {
        internal val entries = mutableListOf<RangeDispatchEntry<C>>()

        init {
            callback()
        }

        fun match(threshold: Float, callback: ItemModelBuilder.() -> Unit) {
            entries.add(RangeDispatchEntry(threshold, ItemModelBuilder(callback)))
        }
    }

    companion object {

    }
}
