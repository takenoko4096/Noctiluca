package io.github.takenoko4096.noctiluca.datagen.model.builder.select

import io.github.takenoko4096.noctiluca.render.model.item.builder.select.BlockStateSelect
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.select.ItemBlockState
import net.minecraft.world.item.Item

class ClientBlockStateSelect<C : Comparable<C>>(itemModelGenerators: ItemModelGenerators, item: Item, select: BlockStateSelect<C>) : ClientSelect<BlockStateSelect<C>, C>(itemModelGenerators, item, select) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.select(
            ItemBlockState(handle.property.name),
            getFallback(),
            getCases { v -> v.toString() }
        )
    }
}
