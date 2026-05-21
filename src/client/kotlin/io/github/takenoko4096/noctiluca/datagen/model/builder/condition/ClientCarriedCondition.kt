package io.github.takenoko4096.noctiluca.datagen.model.builder.condition

import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.CarriedCondition
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.conditional.IsCarried
import net.minecraft.world.item.Item

class ClientCarriedCondition(itemModelGenerators: ItemModelGenerators, item: Item, condition: CarriedCondition) : ClientCondition<CarriedCondition, Unit>(itemModelGenerators, item, condition) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.conditional(
            IsCarried(),
            getOnTrue(),
            getOnFalse()
        )
    }
}
