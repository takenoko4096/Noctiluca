package io.github.takenoko4096.noctiluca.datagen.model.builder.rangedispatch

import io.github.takenoko4096.noctiluca.datagen.model.builder.ClientItemModelHandle
import io.github.takenoko4096.noctiluca.render.model.item.builder.ItemModelBuilder
import io.github.takenoko4096.noctiluca.render.model.item.builder.rangedispatch.RangeDispatch
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.RangeSelectItemModel
import net.minecraft.world.item.Item

abstract class ClientRangeDispatch<T : RangeDispatch<C>, C>(itemModelGenerators: ItemModelGenerators, item: Item, protected val rangeDispatch: RangeDispatch<C>) : ClientItemModelHandle(itemModelGenerators, item, rangeDispatch) {
    protected fun getScale(): Float {
        return rangeDispatch.scale
    }

    protected fun getEntries(): List<RangeSelectItemModel.Entry> {
        return ItemModelBuilder.AccessForClient.getRangeDispatchEntries(rangeDispatch).map {
            RangeSelectItemModel.Entry(
                it.threshold,
                toClient(
                    itemModelGenerators,
                    item,
                    it.model.build()
                ).convert()
            )
        }
    }

    protected fun getFallback(): ItemModel.Unbaked {
        return toClient(
            itemModelGenerators,
            item,
            ItemModelBuilder.AccessForClient.getRangeDispatchFallback(rangeDispatch)
        ).convert()
    }

    companion object {
        internal fun toClientRangeDispatch(itemModelGenerators: ItemModelGenerators, item: Item, rangeDispatch: RangeDispatch<*>): ClientRangeDispatch<*, *> = when (rangeDispatch) {
            else -> throw IllegalStateException()
        }
    }
}
