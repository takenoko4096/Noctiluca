package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.registry.item.ModItemConfiguration
import io.github.takenoko4096.noctiluca.render.TexturePath
import io.github.takenoko4096.noctiluca.render.model.block.BlockModelProvider
import io.github.takenoko4096.noctiluca.render.model.item.ItemModelProvider

@NoctilucaDsl
class ModelsConfiguration internal constructor(internal val configuration: ModBlockConfiguration, callback: ModelsConfiguration.() -> Unit) {
    internal var blockModelConfig: BlockModelConfiguration? = null

    internal var itemModelConfig: ModItemConfiguration.ItemModelConfiguration? = null

    val blockDefaultTexturePath = TexturePath.Companion.blockDefault(configuration.blockResourceKey)

    val itemDefaultTexturePath = TexturePath.Companion.itemDefault(configuration.itemResourceKey)

    val blockModels = BlockModelProvider(configuration.blockResourceKey)

    val itemModels = ItemModelProvider(configuration.itemResourceKey)

    init {
        callback()
    }

    fun block(callback: BlockModelConfiguration.() -> Unit) {
        blockModelConfig = BlockModelConfiguration(configuration, callback)
    }

    fun item(callback: ModItemConfiguration.ItemModelConfiguration.() -> Unit) {
        itemModelConfig = ModItemConfiguration.ItemModelConfiguration(configuration.itemResourceKey, callback)
    }
}
