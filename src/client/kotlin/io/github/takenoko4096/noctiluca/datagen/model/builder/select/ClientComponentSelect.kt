package io.github.takenoko4096.noctiluca.datagen.model.builder.select

import io.github.takenoko4096.noctiluca.render.model.item.builder.select.ComponentSelect
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.select.ComponentContents
import net.minecraft.world.item.Item

class ClientComponentSelect<C : Any>(itemModelGenerators: ItemModelGenerators, item: Item, select: ComponentSelect<C>) : ClientSelect<ComponentSelect<C>, C>(itemModelGenerators, item, select) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.select(
            ComponentContents(handle.componentType),
            getFallback(),
            getCases { it }
        )
    }
}
