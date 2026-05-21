package io.github.takenoko4096.noctiluca.datagen.model.builder.select

import io.github.takenoko4096.noctiluca.render.model.item.builder.select.ContextDimensionSelect
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.select.ContextDimension
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class ClientContextDimensionSelect(itemModelGenerators: ItemModelGenerators, item: Item, select: ContextDimensionSelect) : ClientSelect<ContextDimensionSelect, ResourceKey<Level>>(itemModelGenerators, item, select) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.select(
            ContextDimension(),
            getFallback(),
            getCases { it }
        )
    }
}
