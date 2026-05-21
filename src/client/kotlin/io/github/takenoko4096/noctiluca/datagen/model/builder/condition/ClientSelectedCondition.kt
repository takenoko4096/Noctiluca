package io.github.takenoko4096.noctiluca.datagen.model.builder.condition

import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.SelectedCondition
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.conditional.IsSelected
import net.minecraft.world.item.Item

class ClientSelectedCondition(itemModelGenerators: ItemModelGenerators, item: Item, condition: SelectedCondition) : ClientCondition<SelectedCondition, Unit>(itemModelGenerators, item, condition) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.conditional(
            IsSelected(),
            getOnTrue(),
            getOnFalse()
        )
    }
}
