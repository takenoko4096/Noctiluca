package io.github.takenoko4096.noctiluca.datagen.model

import io.github.takenoko4096.noctiluca.datagen.model.builtin.ClientBuiltinBlockModel
import io.github.takenoko4096.noctiluca.datagen.model.builtin.ClientBuiltinItemModel
import io.github.takenoko4096.noctiluca.datagen.model.custom.ClientParentExtensionBlockModel
import io.github.takenoko4096.noctiluca.datagen.model.custom.ClientParentExtensionItemModel
import io.github.takenoko4096.noctiluca.render.model.NonClientModel
import io.github.takenoko4096.noctiluca.render.model.builtin.NonClientBuiltinModel
import io.github.takenoko4096.noctiluca.render.model.custom.NonClientParentExtensionModel
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.Objects

abstract class ClientModel {
    protected abstract val template: ModelTemplate

    protected abstract val mapping: TextureMapping

    abstract val identifier: Identifier

    override fun hashCode(): Int {
        return Objects.hash(identifier)
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is ClientModel) false else identifier == other.identifier
    }

    companion object {
        private val CREATED_MODELS = mutableMapOf<NonClientModel, ClientModel>()

        fun getOrCreate(item: Item, model: NonClientModel, generators: ItemModelGenerators): ClientModel {
            if (CREATED_MODELS.contains(model)) {
                return CREATED_MODELS[model]!!
            }

            val clientModel = when (model) {
                is NonClientBuiltinModel -> ClientBuiltinItemModel(item, model, generators)
                is NonClientParentExtensionModel -> ClientParentExtensionItemModel(item, model, generators)
                else -> throw IllegalStateException("NEVER HAPPENS")
            }

            CREATED_MODELS[model] = clientModel
            return clientModel
        }

        fun getOrCreate(block: Block, model: NonClientModel, generators: BlockModelGenerators): ClientModel {
            if (CREATED_MODELS.contains(model)) {
                return CREATED_MODELS[model]!!
            }

            val clientModel = when (model) {
                is NonClientBuiltinModel -> {
                    ClientBuiltinBlockModel(block, model, generators)
                }
                is NonClientParentExtensionModel -> {
                    ClientParentExtensionBlockModel(block, model, generators)
                }
                else -> throw IllegalStateException("NEVER HAPPENS")
            }

            CREATED_MODELS[model] = clientModel
            return clientModel
        }
    }
}
