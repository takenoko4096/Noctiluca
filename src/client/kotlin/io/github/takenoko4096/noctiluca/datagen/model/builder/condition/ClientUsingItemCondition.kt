package io.github.takenoko4096.noctiluca.datagen.model.builder.condition

import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.UsingItemCondition
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.conditional.IsUsingItem
import net.minecraft.world.item.Item

class ClientUsingItemCondition(itemModelGenerators: ItemModelGenerators, item: Item, condition: UsingItemCondition) : ClientCondition<UsingItemCondition, Unit>(itemModelGenerators, item, condition) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.conditional(
            IsUsingItem(),
            getOnTrue(),
            getOnFalse()
        )
    }
}
