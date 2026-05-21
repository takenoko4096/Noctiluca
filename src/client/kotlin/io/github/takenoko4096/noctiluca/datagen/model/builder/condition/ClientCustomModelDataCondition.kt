package io.github.takenoko4096.noctiluca.datagen.model.builder.condition

import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.CustomModelDataCondition
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.conditional.CustomModelDataProperty
import net.minecraft.world.item.Item

class ClientCustomModelDataCondition(itemModelGenerators: ItemModelGenerators, item: Item, condition: CustomModelDataCondition) : ClientCondition<CustomModelDataCondition, Int>(itemModelGenerators, item, condition) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.conditional(
            CustomModelDataProperty(condition.index),
            getOnTrue(),
            getOnFalse()
        )
    }
}
