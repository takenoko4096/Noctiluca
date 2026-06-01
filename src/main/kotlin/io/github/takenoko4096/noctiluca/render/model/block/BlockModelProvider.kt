package io.github.takenoko4096.noctiluca.render.model.block

import io.github.takenoko4096.noctiluca.render.TexturePath
import io.github.takenoko4096.noctiluca.render.model.ModelOptions
import io.github.takenoko4096.noctiluca.render.model.ModelProvider
import io.github.takenoko4096.noctiluca.render.model.builtin.NonClientBuiltinModel
import io.github.takenoko4096.noctiluca.render.model.builtin.NonClientBuiltinModelTemplate
import io.github.takenoko4096.noctiluca.render.model.builtin.NonClientBuiltinTextureSlot
import io.github.takenoko4096.noctiluca.render.model.custom.NonClientParentExtensionModel
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block

class BlockModelProvider internal constructor(resourceKey: ResourceKey<Block>) : ModelProvider<Block>(resourceKey) {
    fun cube(particle: TexturePath, north: TexturePath, south: TexturePath, east: TexturePath, west: TexturePath, up: TexturePath, down: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
        return NonClientBuiltinModel(
            resourceKey,
            NonClientBuiltinModelTemplate.CUBE,
            mapOf(
                NonClientBuiltinTextureSlot.PARTICLE to particle,
                NonClientBuiltinTextureSlot.NORTH to north,
                NonClientBuiltinTextureSlot.SOUTH to south,
                NonClientBuiltinTextureSlot.EAST to east,
                NonClientBuiltinTextureSlot.WEST to west,
                NonClientBuiltinTextureSlot.UP to up,
                NonClientBuiltinTextureSlot.DOWN to down
            ),
            ModelOptions(callback)
        )
    }

    fun cubeColumn(side: TexturePath, end: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
        return NonClientBuiltinModel(
            resourceKey,
            NonClientBuiltinModelTemplate.CUBE_COLUMN,
            mapOf(
                NonClientBuiltinTextureSlot.SIDE to side,
                NonClientBuiltinTextureSlot.END to end
            ),
            ModelOptions(callback)
        )
    }

    fun cubeAll(all: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
        return NonClientBuiltinModel(
            resourceKey,
            NonClientBuiltinModelTemplate.CUBE_ALL,
            mapOf(
                NonClientBuiltinTextureSlot.ALL to all
            ),
            ModelOptions(callback)
        )
    }

    fun crop(crop: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
        return NonClientBuiltinModel(
            resourceKey,
            NonClientBuiltinModelTemplate.CROP,
            mapOf(
                NonClientBuiltinTextureSlot.CROP to crop
            ),
            ModelOptions(callback)
        )
    }

    fun slabTop(top: TexturePath, side: TexturePath, bottom: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
        return NonClientBuiltinModel(
            resourceKey,
            NonClientBuiltinModelTemplate.SLAB_TOP,
            mapOf(
                NonClientBuiltinTextureSlot.TOP to top,
                NonClientBuiltinTextureSlot.SIDE to side,
                NonClientBuiltinTextureSlot.BOTTOM to bottom
            ),
            ModelOptions(callback)
        )
    }

    fun slabBottom(top: TexturePath, side: TexturePath, bottom: TexturePath, callback: ModelOptions.() -> Unit = {}): NonClientBuiltinModel {
        return NonClientBuiltinModel(
            resourceKey,
            NonClientBuiltinModelTemplate.SLAB_BOTTOM,
            mapOf(
                NonClientBuiltinTextureSlot.TOP to top,
                NonClientBuiltinTextureSlot.SIDE to side,
                NonClientBuiltinTextureSlot.BOTTOM to bottom
            ),
            ModelOptions(callback)
        )
    }

    fun fromParent(modelTemplate: Identifier, textureMapping: Map<String, TexturePath>, callback: ModelOptions.() -> Unit = {}): NonClientParentExtensionModel {
        return NonClientParentExtensionModel(
            resourceKey,
            modelTemplate,
            textureMapping,
            ModelOptions(callback)
        )
    }
}
