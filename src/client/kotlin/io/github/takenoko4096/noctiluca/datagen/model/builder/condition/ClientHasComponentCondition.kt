package io.github.takenoko4096.noctiluca.datagen.model.builder.condition

import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.HasComponentCondition
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.conditional.HasComponent
import net.minecraft.world.item.Item

class ClientHasComponentCondition<C : Any>(itemModelGenerators: ItemModelGenerators, item: Item, condition: HasComponentCondition<C>) : ClientCondition<HasComponentCondition<C>, C>(itemModelGenerators, item, condition) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.conditional(
            HasComponent(condition.type, condition.ignoreDefault),
            getOnTrue(),
            getOnFalse()
        )
    }
}
