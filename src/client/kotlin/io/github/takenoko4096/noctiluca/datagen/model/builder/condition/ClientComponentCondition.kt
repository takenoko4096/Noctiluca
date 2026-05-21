package io.github.takenoko4096.noctiluca.datagen.model.builder.condition

import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.ComponentCondition
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.conditional.ComponentMatches
import net.minecraft.core.component.predicates.DataComponentPredicate
import net.minecraft.world.item.Item

class ClientComponentCondition<C : DataComponentPredicate>(itemModelGenerators: ItemModelGenerators, item: Item, condition: ComponentCondition<C>) : ClientCondition<ComponentCondition<C>, C>(itemModelGenerators, item, condition) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.conditional(
            ComponentMatches(DataComponentPredicate.Single(
                condition.type,
                condition.value
            )),
            getOnTrue(),
            getOnFalse()
        )
    }
}
