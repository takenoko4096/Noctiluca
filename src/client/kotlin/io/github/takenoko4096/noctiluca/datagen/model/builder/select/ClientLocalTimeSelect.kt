package io.github.takenoko4096.noctiluca.datagen.model.builder.select

import io.github.takenoko4096.noctiluca.render.model.item.builder.select.LocalTimeSelect
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.properties.select.LocalTime
import net.minecraft.world.item.Item
import java.util.Optional

class ClientLocalTimeSelect(itemModelGenerators: ItemModelGenerators, item: Item, select: LocalTimeSelect) : ClientSelect<LocalTimeSelect, String>(itemModelGenerators, item, select) {
    override fun convert(): ItemModel.Unbaked {
        return ItemModelUtils.select(
            LocalTime.create(handle.format, handle.locale.name, Optional.of(handle.timeZone)),
            getFallback(),
            getCases { it }
        )
    }
}
