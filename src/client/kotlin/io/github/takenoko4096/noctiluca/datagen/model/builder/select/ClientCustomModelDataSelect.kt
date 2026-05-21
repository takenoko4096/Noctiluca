package io.github.takenoko4096.noctiluca.datagen.model.builder.select

import io.github.takenoko4096.noctiluca.render.model.item.builder.select.CustomModelDataSelect
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.select.CustomModelDataProperty
import net.minecraft.world.item.Item

class ClientCustomModelDataSelect(itemModelGenerators: ItemModelGenerators, item: Item, select: CustomModelDataSelect) : ClientSelect<CustomModelDataSelect, String>(itemModelGenerators, item, select) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.select(
            CustomModelDataProperty(handle.index),
            getFallback(),
            getCases { it }
        )
    }
}
