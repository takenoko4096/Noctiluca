package io.github.takenoko4096.noctiluca.datagen.providers

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.mojang.serialization.JsonOps
import io.github.takenoko4096.noctiluca.Noctiluca
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.datagen.model.BlockModelVariantsRegistrar
import io.github.takenoko4096.noctiluca.datagen.model.builder.ClientItemModelHandle
import io.github.takenoko4096.noctiluca.registry.block.ModBlockConfiguration
import io.github.takenoko4096.noctiluca.registry.block.SingleArgBlockModel
import io.github.takenoko4096.noctiluca.registry.item.ModItemConfiguration
import io.github.takenoko4096.noctiluca.registry.item.ModItemRegistry
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.client.data.models.model.TexturedModel
import net.minecraft.client.resources.model.sprite.Material
import net.minecraft.resources.Identifier
import java.util.function.Consumer

class NoctilucaModelProvider(private val mod: NoctilucaModInitializer, output: FabricPackOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockModelGenerators: BlockModelGenerators) {
        val blockRegistry = mod.blockRegistry

        /*blockModelGenerators.modelOutput.accept(Noctiluca.identifierOf("block/custom_portal")) {
            val result = JsonObject()
            val elements = JsonArray()
            elements.add(JsonParser.parseString("""
                    {
                        "from": [ 6, 0, 0 ],
                        "to": [ 10, 16, 16 ],
                        "faces": {
                            "east": {
                                "uv": [ 0, 0, 16, 16 ],
                                "texture": "#portal",
                                "tintindex": 0
                            },
                            "west": {
                                "uv": [ 0, 0, 16, 16 ],
                                "texture": "#portal",
                                "tintindex": 0
                            }
                        }
                    }
                    """.trimIndent()))
            result.add("elements", elements)
            val textureObj = JsonObject()
            textureObj.add("portal", JsonPrimitive("minecraft:block/nether_portal_ew"))
            textureObj.add("particle", JsonPrimitive("minecraft:block/nether_portal_ew"))
            result.add("textures", textureObj)
            return@accept result
        }*/

        for (configuration in blockRegistry.getConfigurations()) {
            val block = blockRegistry.getBlock(configuration.blockResourceKey)
            val accessor = ModBlockConfiguration.getAccessorForClient(configuration)
            configuration.withItem()

            val model = accessor.blockModelLegacy()
            when (model?.textureMap) {
                SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_CUBE -> {
                    blockModelGenerators.createTrivialCube(block)
                }
                SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN -> {
                    blockModelGenerators.createTrivialBlock(block, TexturedModel.COLUMN)
                }
                SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN_ALT -> {
                    blockModelGenerators.createTrivialBlock(block, TexturedModel.COLUMN_ALT)
                }
                SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN_HORIZONTAL -> {
                    blockModelGenerators.createTrivialBlock(block, TexturedModel.COLUMN_HORIZONTAL)
                }
                SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN_HORIZONTAL_ALT -> {
                    blockModelGenerators.createTrivialBlock(block, TexturedModel.COLUMN_HORIZONTAL_ALT)
                }
                SingleArgBlockModel.SingleArgBlockTextureMap.ANVIL -> {
                    blockModelGenerators.createAnvil(block)
                }
                SingleArgBlockModel.SingleArgBlockTextureMap.DOOR -> {
                    blockModelGenerators.createDoor(block)
                }
                SingleArgBlockModel.SingleArgBlockTextureMap.LANTERN -> {
                    blockModelGenerators.createLantern(block)
                }
                null -> {}
            }

            val variants = accessor.blockModelVariants()

            val registrar = BlockModelVariantsRegistrar(
                blockModelGenerators,
                block,
                accessor.blockItemModel(),
                variants,
                accessor.family()
            )
            registrar.register()
        }
    }

    override fun generateItemModels(itemModelGenerators: ItemModelGenerators) {
        val itemRegistry: ModItemRegistry = mod.itemRegistry

        for (configuration in itemRegistry.getConfigurations()) {
            val item = itemRegistry.getItem(configuration.itemResourceKey)
            val accessor = ModItemConfiguration.Companion.getAccessor(configuration)

            ClientItemModelHandle.Companion.registerModel(itemModelGenerators, item, accessor.getModelHandle())
        }
    }

    override fun getName(): String = "StarlightModelProvider"
}