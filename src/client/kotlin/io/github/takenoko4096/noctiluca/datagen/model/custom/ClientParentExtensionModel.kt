package io.github.takenoko4096.noctiluca.datagen.model.custom

import io.github.takenoko4096.noctiluca.datagen.model.ClientModel
import io.github.takenoko4096.noctiluca.render.model.custom.NonClientParentExtensionModel
import net.fabricmc.fabric.impl.client.model.loading.UnbakedModelJsonDeserializer
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.client.model.Model
import net.minecraft.client.resources.model.ModelDiscovery
import net.minecraft.client.resources.model.UnbakedModel
import net.minecraft.client.resources.model.cuboid.CuboidModel
import net.minecraft.client.resources.model.cuboid.UnbakedCuboidGeometry
import net.minecraft.client.resources.model.sprite.Material
import net.minecraft.resources.Identifier
import java.util.Optional

abstract class ClientParentExtensionModel(nonClient: NonClientParentExtensionModel): ClientModel() {
    final override val template: ModelTemplate

    final override val mapping: TextureMapping

    abstract override val identifier: Identifier

    init {
        val mappingBase = nonClient.mapping
            .mapKeys { TextureSlot.create(it.key) }
            .mapValues { it.value.identifier }

        template = ModelTemplate(
            Optional.of(nonClient.parent),
            Optional.empty(),
            *mappingBase.keys.toTypedArray()
        )

        mapping = TextureMapping().apply {
            mappingBase.forEach {
                put(it.key, Material(it.value))
            }
        }
    }
}
