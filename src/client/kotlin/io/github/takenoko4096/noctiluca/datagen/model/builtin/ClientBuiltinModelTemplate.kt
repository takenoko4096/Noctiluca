package io.github.takenoko4096.noctiluca.datagen.model.builtin

import io.github.takenoko4096.noctiluca.render.model.builtin.NonClientBuiltinModelTemplate
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.ModelTemplates

enum class ClientBuiltinModelTemplate(private val nonClient: NonClientBuiltinModelTemplate, private val client: ModelTemplate) {
    CUBE(NonClientBuiltinModelTemplate.CUBE, ModelTemplates.CUBE),
    CUBE_COLUMN(NonClientBuiltinModelTemplate.CUBE_COLUMN, ModelTemplates.CUBE_COLUMN),
    CUBE_ALL(NonClientBuiltinModelTemplate.CUBE_ALL, ModelTemplates.CUBE_ALL),
    CROP(NonClientBuiltinModelTemplate.CROP, ModelTemplates.CROP),
    SLAB_TOP(NonClientBuiltinModelTemplate.SLAB_TOP, ModelTemplates.SLAB_TOP),
    SLAB_BOTTOM(NonClientBuiltinModelTemplate.SLAB_BOTTOM, ModelTemplates.SLAB_BOTTOM),
    FLAT_ITEM(NonClientBuiltinModelTemplate.FLAT_ITEM, ModelTemplates.FLAT_ITEM),
    FLAT_HANDHELD_ITEM(NonClientBuiltinModelTemplate.FLAT_HANDHELD_ITEM, ModelTemplates.FLAT_HANDHELD_ITEM),
    FLAT_HANDHELD_ROD_ITEM(NonClientBuiltinModelTemplate.FLAT_HANDHELD_ROD_ITEM, ModelTemplates.FLAT_HANDHELD_ROD_ITEM),
    FLAT_HANDHELD_MACE_ITEM(NonClientBuiltinModelTemplate.FLAT_HANDHELD_MACE_ITEM, ModelTemplates.FLAT_HANDHELD_MACE_ITEM);

    companion object {
        fun convert(nonClient: NonClientBuiltinModelTemplate): ModelTemplate {
            for (entry in entries) {
                if (entry.nonClient == nonClient) return entry.client
            }

            throw IllegalArgumentException("Not implemented (client side)")
        }
    }
}
