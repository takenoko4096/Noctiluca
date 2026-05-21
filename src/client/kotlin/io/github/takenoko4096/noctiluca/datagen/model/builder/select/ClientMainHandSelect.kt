package io.github.takenoko4096.noctiluca.datagen.model.builder.select

import io.github.takenoko4096.noctiluca.render.model.item.builder.select.MainHandSelect
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.select.MainHand
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.item.Item

class ClientMainHandSelect(itemModelGenerators: ItemModelGenerators, item: Item, select: MainHandSelect) : ClientSelect<MainHandSelect, HumanoidArm>(itemModelGenerators, item, select) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.select(
            MainHand(),
            getFallback(),
            getCases { it }
        )
    }
}
