package io.github.takenoko4096.noctiluca.datagen.model.builder.select

import io.github.takenoko4096.noctiluca.render.model.item.builder.select.ContextEntityTypeSelect
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.select.ContextEntityType
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item

class ClientContextEntityTypeSelect(itemModelGenerators: ItemModelGenerators, item: Item, select: ContextEntityTypeSelect) : ClientSelect<ContextEntityTypeSelect, ResourceKey<EntityType<*>>>(itemModelGenerators, item, select) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.select(
            ContextEntityType(),
            getFallback(),
            getCases { it }
        )
    }
}
