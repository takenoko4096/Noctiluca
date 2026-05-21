package io.github.takenoko4096.noctiluca.datagen.model.builtin

import io.github.takenoko4096.noctiluca.datagen.model.ClientModel
import io.github.takenoko4096.noctiluca.render.model.builtin.NonClientBuiltinModel
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.resources.model.sprite.Material
import net.minecraft.resources.Identifier

abstract class ClientBuiltinModel(nonClient: NonClientBuiltinModel) : ClientModel() {
    final override val template: ModelTemplate

    final override val mapping: TextureMapping

    abstract override val identifier: Identifier

    init {
        val mappingBase = nonClient.mapping
            .mapKeys {
                ClientBuiltinTextureSlot.convert(it.key)
            }
            .mapValues {
                it.value.identifier
            }

        template = ClientBuiltinModelTemplate.convert(nonClient.template)

        mapping = TextureMapping().apply {
            mappingBase.forEach {
                put(it.key, Material(it.value))
            }
        }
    }
}
