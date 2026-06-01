package io.github.takenoko4096.noctiluca.render.model.item

import io.github.takenoko4096.noctiluca.render.TexturePath
import io.github.takenoko4096.noctiluca.render.model.ModelOptions
import io.github.takenoko4096.noctiluca.render.model.ModelProvider
import io.github.takenoko4096.noctiluca.render.model.builtin.NonClientBuiltinModel
import io.github.takenoko4096.noctiluca.render.model.builtin.NonClientBuiltinModelTemplate
import io.github.takenoko4096.noctiluca.render.model.builtin.NonClientBuiltinTextureSlot
import io.github.takenoko4096.noctiluca.render.model.custom.NonClientParentExtensionModel
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item

class ItemModelProvider internal constructor(resourceKey: ResourceKey<Item>) : ModelProvider<Item>(resourceKey) {
    fun generated(layer0: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
        return NonClientBuiltinModel(
            resourceKey,
            NonClientBuiltinModelTemplate.FLAT_ITEM,
            mapOf(
                NonClientBuiltinTextureSlot.LAYER0 to layer0
            ),
            ModelOptions(callback)
        )
    }

    fun handheld(layer0: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
        return NonClientBuiltinModel(
            resourceKey,
            NonClientBuiltinModelTemplate.FLAT_HANDHELD_ITEM,
            mapOf(
                NonClientBuiltinTextureSlot.LAYER0 to layer0
            ),
            ModelOptions(callback)
        )
    }

    fun rod(layer0: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
        return NonClientBuiltinModel(
            resourceKey,
            NonClientBuiltinModelTemplate.FLAT_ITEM,
            mapOf(
                NonClientBuiltinTextureSlot.LAYER0 to layer0
            ),
            ModelOptions(callback)
        )
    }

    fun mace(layer0: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
        return NonClientBuiltinModel(
            resourceKey,
            NonClientBuiltinModelTemplate.FLAT_HANDHELD_MACE_ITEM,
            mapOf(
                NonClientBuiltinTextureSlot.LAYER0 to layer0
            ),
            ModelOptions(callback)
        )
    }

    fun custom(modelTemplate: Identifier, textureMapping: Map<String, TexturePath>, callback: ModelOptions.() -> Unit = {}): NonClientParentExtensionModel {
        return NonClientParentExtensionModel(
            resourceKey,
            modelTemplate,
            textureMapping,
            ModelOptions(callback)
        )
    }
}
