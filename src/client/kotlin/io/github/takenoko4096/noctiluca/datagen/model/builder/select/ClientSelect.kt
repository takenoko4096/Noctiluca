package io.github.takenoko4096.noctiluca.datagen.model.builder.select

import io.github.takenoko4096.noctiluca.datagen.model.builder.ClientItemModelHandle
import io.github.takenoko4096.noctiluca.render.model.item.builder.ItemModelBuilder
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.BlockStateSelect
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.ChargeTypeSelect
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.ComponentSelect
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.ContextDimensionSelect
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.ContextEntityTypeSelect
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.CustomModelDataSelect
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.DisplayContextSelect
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.LocalTimeSelect
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.MainHandSelect
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.Select
import io.github.takenoko4096.noctiluca.render.model.item.builder.select.TrimMaterialSelect
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.SelectItemModel
import net.minecraft.world.item.Item

abstract class ClientSelect<T : Select<C>, C> protected constructor(itemModelGenerators: ItemModelGenerators, item: Item, override val handle: T) : ClientItemModelHandle(itemModelGenerators, item, handle) {
    protected fun <T : Any> getCases(mapper: (C) -> T): List<SelectItemModel.SwitchCase<T>> {
        return ItemModelBuilder.AccessForClient.getSelectCases(handle).map {
            ItemModelUtils.`when`(
                it.`when`.map(mapper).toList(),
                toClientSelect(itemModelGenerators, item, handle).convert()
            )
        }
    }

    protected fun getFallback(): ItemModel.Unbaked {
        return toClient(
            itemModelGenerators,
            item,
            ItemModelBuilder.AccessForClient.getSelectFallback(handle)
        ).convert()
    }

    companion object {
        internal fun toClientSelect(itemModelGenerators: ItemModelGenerators, item: Item, select: Select<*>): ClientSelect<*, *> = when (select) {
            is BlockStateSelect<*> -> ClientBlockStateSelect(itemModelGenerators, item, select)
            is ChargeTypeSelect -> ClientChargeTypeSelect(itemModelGenerators, item, select)
            is ComponentSelect<*> -> ClientComponentSelect(itemModelGenerators, item, select)
            is ContextDimensionSelect -> ClientContextDimensionSelect(itemModelGenerators, item, select)
            is ContextEntityTypeSelect -> ClientContextEntityTypeSelect(itemModelGenerators, item, select)
            is CustomModelDataSelect -> ClientCustomModelDataSelect(itemModelGenerators, item, select)
            is DisplayContextSelect -> ClientDisplayContextSelect(itemModelGenerators, item, select)
            is LocalTimeSelect -> ClientLocalTimeSelect(itemModelGenerators, item, select)
            is MainHandSelect -> ClientMainHandSelect(itemModelGenerators, item, select)
            is TrimMaterialSelect -> ClientTrimMaterialSelect(itemModelGenerators, item, select)
            else -> throw IllegalStateException()
        }
    }
}
