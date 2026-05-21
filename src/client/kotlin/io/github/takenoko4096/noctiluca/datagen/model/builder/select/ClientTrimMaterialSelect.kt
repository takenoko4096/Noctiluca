package io.github.takenoko4096.noctiluca.datagen.model.builder.select

import io.github.takenoko4096.noctiluca.render.model.item.builder.select.TrimMaterialSelect
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.equipment.trim.TrimMaterial

class ClientTrimMaterialSelect(itemModelGenerators: ItemModelGenerators, item: Item, select: TrimMaterialSelect) : ClientSelect<TrimMaterialSelect, ResourceKey<TrimMaterial>>(itemModelGenerators, item, select) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.select(
            TrimMaterialProperty(),
            getFallback(),
            getCases { it }
        )
    }
}
