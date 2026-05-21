package io.github.takenoko4096.noctiluca.datagen.model.builder.select

import io.github.takenoko4096.noctiluca.render.model.item.builder.select.ChargeTypeSelect
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.select.Charge
import net.minecraft.world.item.CrossbowItem
import net.minecraft.world.item.Item

class ClientChargeTypeSelect(itemModelGenerators: ItemModelGenerators, item: Item, select: ChargeTypeSelect) : ClientSelect<ChargeTypeSelect, CrossbowItem.ChargeType>(itemModelGenerators, item, select) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.select(
            Charge(),
            getFallback(),
            getCases { it }
        )
    }
}
