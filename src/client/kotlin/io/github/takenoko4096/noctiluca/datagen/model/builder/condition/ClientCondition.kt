package io.github.takenoko4096.noctiluca.datagen.model.builder.condition

import io.github.takenoko4096.noctiluca.datagen.model.builder.ClientItemModelHandle
import io.github.takenoko4096.noctiluca.render.model.item.builder.ItemModelBuilder
import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.BrokenCondition
import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.CarriedCondition
import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.ComponentCondition
import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.Condition
import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.CustomModelDataCondition
import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.DamagedCondition
import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.ExtendedViewCondition
import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.HasComponentCondition
import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.SelectedCondition
import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.UsingItemCondition
import io.github.takenoko4096.noctiluca.render.model.item.builder.condition.ViewEntityCondition
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.world.item.Item

abstract class ClientCondition<T : Condition<C>, C> protected constructor(itemModelGenerators: ItemModelGenerators, item: Item, protected val condition: T) : ClientItemModelHandle(itemModelGenerators, item, condition) {
    protected fun getOnTrue(): ItemModel.Unbaked {
        return toClient(
            itemModelGenerators,
            item,
            ItemModelBuilder.AccessForClient.getConditionOnTrue(condition)
        ).convert()
    }

    protected fun getOnFalse(): ItemModel.Unbaked {
        return toClient(
            itemModelGenerators,
            item,
            ItemModelBuilder.AccessForClient.getConditionOnFalse(condition)
        ).convert()
    }

    companion object {
        internal fun toClientCondition(itemModelGenerators: ItemModelGenerators, item: Item, condition: Condition<*>): ClientCondition<*, *> = when (condition) {
            is BrokenCondition -> ClientBrokenCondition(itemModelGenerators, item, condition)
            is CarriedCondition -> ClientCarriedCondition(itemModelGenerators, item, condition)
            is ComponentCondition<*> -> ClientComponentCondition(itemModelGenerators, item, condition)
            is CustomModelDataCondition -> ClientCustomModelDataCondition(itemModelGenerators, item, condition)
            is DamagedCondition -> ClientDamagedCondition(itemModelGenerators, item, condition)
            is ExtendedViewCondition -> ClientExtendedViewCondition(itemModelGenerators, item, condition)
            is HasComponentCondition<*> -> ClientHasComponentCondition(itemModelGenerators, item, condition)
            is SelectedCondition -> ClientSelectedCondition(itemModelGenerators, item, condition)
            is UsingItemCondition -> ClientUsingItemCondition(itemModelGenerators, item ,condition)
            is ViewEntityCondition -> ClientViewEntityCondition(itemModelGenerators, item, condition)
            else -> throw IllegalStateException()
        }
    }
}
