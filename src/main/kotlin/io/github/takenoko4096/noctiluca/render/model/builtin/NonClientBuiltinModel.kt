package io.github.takenoko4096.noctiluca.render.model.builtin

import io.github.takenoko4096.noctiluca.render.TexturePath
import io.github.takenoko4096.noctiluca.render.model.ModelOptions
import io.github.takenoko4096.noctiluca.render.model.NonClientModel
import net.minecraft.resources.ResourceKey

class NonClientBuiltinModel(
    resourceKey: ResourceKey<*>,
    val template: NonClientBuiltinModelTemplate,
    val mapping: Map<NonClientBuiltinTextureSlot, TexturePath>,
    options: ModelOptions
) : NonClientModel(resourceKey, options) {
    init {
        for (slot in template.textureSlots) {
            if (!mapping.contains(slot)) {
                throw IllegalArgumentException("テクスチャスロット '$slot' がビルトインモデルテンプレート '$template' に不足しています")
            }
        }
    }
}
