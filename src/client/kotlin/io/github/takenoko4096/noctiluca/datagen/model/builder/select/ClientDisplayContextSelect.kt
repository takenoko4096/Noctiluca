package io.github.takenoko4096.noctiluca.datagen.model.builder.select

import io.github.takenoko4096.noctiluca.render.model.item.builder.select.DisplayContextSelect
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.select.DisplayContext
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemDisplayContext

class ClientDisplayContextSelect(itemModelGenerators: ItemModelGenerators, item: Item, select: DisplayContextSelect) : ClientSelect<DisplayContextSelect, ItemDisplayContext>(itemModelGenerators, item, select) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.select(
            DisplayContext(),
            getFallback(),
            getCases { it }
        )
    }
}
