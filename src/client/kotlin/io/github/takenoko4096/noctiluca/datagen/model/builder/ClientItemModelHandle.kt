package io.github.takenoko4096.noctiluca.datagen.model.builder

import io.github.takenoko4096.noctiluca.datagen.model.ClientModel
import io.github.takenoko4096.noctiluca.datagen.model.builder.condition.ClientCondition
import io.github.takenoko4096.noctiluca.datagen.model.builder.rangedispatch.ClientRangeDispatch
import io.github.takenoko4096.noctiluca.datagen.model.builder.select.ClientSelect
import io.github.takenoko4096.noctiluca.render.model.item.builder.End
import io.github.takenoko4096.noctiluca.render.model.item.builder.ItemModelHandle
import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.Condition
import io.github.takenoko4096.noctiluca.render.model.item.builder.rangedispatch.RangeDispatch
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.Select
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.world.item.Item

abstract class ClientItemModelHandle internal constructor(
    protected val itemModelGenerators: ItemModelGenerators,
    protected val item: Item,
    protected open val handle: ItemModelHandle
) {
    abstract fun convert(): ItemModel.Unbaked

    class ClientEnd(itemModelGenerators: ItemModelGenerators, item: Item, private val end: End) : ClientItemModelHandle(itemModelGenerators, item, end) {
        override fun convert(): ItemModel.Unbaked {
            val model = ClientModel.getOrCreate(item, end.model, itemModelGenerators)
            return ItemModelUtils.plainModel(model.identifier)
        }
    }

    companion object {
        internal fun toClient(itemModelGenerators: ItemModelGenerators, item: Item, handle: ItemModelHandle): ClientItemModelHandle = when (handle) {
            is End -> ClientEnd(itemModelGenerators, item, handle)
            is Select<*> -> ClientSelect.toClientSelect(itemModelGenerators, item, handle)
            is Condition<*> -> ClientCondition.toClientCondition(itemModelGenerators, item, handle)
            is RangeDispatch<*> -> ClientRangeDispatch.toClientRangeDispatch(itemModelGenerators, item, handle)
            else -> throw IllegalStateException()
        }

        fun registerModel(itemModelGenerators: ItemModelGenerators, item: Item, handle: ItemModelHandle) {
            itemModelGenerators.itemModelOutput.accept(item, toClient(itemModelGenerators, item, handle).convert())
        }
    }
}
