package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.registry.item.ModItemConfiguration
import io.github.takenoko4096.noctiluca.render.TexturePath
import io.github.takenoko4096.noctiluca.render.model.block.BlockModelProvider
import io.github.takenoko4096.noctiluca.render.model.block.NonClientBlockModelVariant
import io.github.takenoko4096.noctiluca.render.model.block.PropertyVariants
import io.github.takenoko4096.noctiluca.render.model.block.PropertyVariants0
import io.github.takenoko4096.noctiluca.render.model.block.PropertyVariants1
import io.github.takenoko4096.noctiluca.render.model.block.PropertyVariants2
import io.github.takenoko4096.noctiluca.render.model.item.ItemModelProvider
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import org.jetbrains.annotations.ApiStatus

@NoctilucaDsl
class BlockRenderingConfiguration internal constructor(private val configuration: ModBlockConfiguration) {
    internal var modelConfig: ModelsConfiguration? = null

    internal var tintConfig: TintConfiguration = TintConfiguration {}

    fun models(callback: ModelsConfiguration.() -> Unit) {
        modelConfig = ModelsConfiguration(configuration, callback)
    }

    fun color(callback: TintConfiguration.() -> Unit) {
        tintConfig = TintConfiguration(callback)
    }

    @NoctilucaDsl
    class TintConfiguration internal constructor(callback: TintConfiguration.() -> Unit) {
        internal var defaultColorGetter: (BlockState) -> Int = { -1 }

        internal var colorGetter: (BlockState, BlockPos, Level) -> Int = { a, b, c -> -1 }

        internal var particleColorGetter: (BlockState, BlockPos, Level) -> Int = { a, b, c -> -1 }

        init {
            callback()
        }

        fun default(callback: (BlockState) -> Int) {
            defaultColorGetter = callback
        }

        fun inWorld(callback: (BlockState, BlockPos, Level) -> Int) {
            colorGetter = callback
        }

        fun terrainParticle(callback: (BlockState, BlockPos, Level) -> Int) {
            particleColorGetter = callback
        }
    }

    @NoctilucaDsl
    class ModelsConfiguration internal constructor(internal val configuration: ModBlockConfiguration, callback: ModelsConfiguration.() -> Unit) {
        internal var blockModelConfig: BlockModelConfiguration? = null

        internal var itemModelConfig: ModItemConfiguration.ItemModelConfiguration? = null

        val blockDefaultTexturePath = TexturePath.blockDefault(configuration.blockResourceKey)

        val itemDefaultTexturePath = TexturePath.itemDefault(configuration.itemResourceKey)

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

    @NoctilucaDsl
    class BlockModelConfiguration internal constructor(internal val configuration: ModBlockConfiguration, callback: BlockModelConfiguration.() -> Unit) {
        internal var variants: PropertyVariants? = null

        init {
            callback()
        }

        fun always(variant: NonClientBlockModelVariant) {
            variants = PropertyVariants0(variant)
        }

        fun <T : Comparable<T>> variants(property: Property<T>, callback: PropertyVariants1<T>.() -> Unit) {
            val vp1 = PropertyVariants1(property)
            vp1.callback()
            variants = vp1
        }

        fun <T: Comparable<T>, U : Comparable<U>> variants(property1: Property<T>, property2: Property<U>, callback: PropertyVariants2<T, U>.() -> Unit) {
            val vp2 = PropertyVariants2(property1, property2)
            vp2.callback()
            variants = vp2
        }

        /**
         * ↓のコードは PropertyVariants 自動操作に置き替える？ それとも削除？
         */
        internal var model: SingleArgBlockModel? = null

        private fun singleArg(textureMap: SingleArgBlockModel.SingleArgBlockTextureMap) {
            model = SingleArgBlockModel(textureMap)
        }

        @ApiStatus.Obsolete
        fun trivialCube() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_CUBE)
        }

        @ApiStatus.Obsolete
        fun trivialColumn() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN)
        }

        @ApiStatus.Obsolete
        fun trivialColumnAlt() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN_ALT)
        }

        @ApiStatus.Obsolete
        fun trivialColumnHorizontal() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN_HORIZONTAL)
        }

        @ApiStatus.Obsolete
        fun trivialColumnHorizontalAlt() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.TRIVIAL_COLUMN_HORIZONTAL_ALT)
        }

        @ApiStatus.Obsolete
        fun anvil() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.ANVIL)
        }

        @ApiStatus.Obsolete
        fun door() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.DOOR)
        }

        @ApiStatus.Obsolete
        fun lantern() {
            singleArg(SingleArgBlockModel.SingleArgBlockTextureMap.LANTERN)
        }
    }

    class SingleArgBlockModel internal constructor(val textureMap: SingleArgBlockTextureMap) {
        enum class SingleArgBlockTextureMap {
            TRIVIAL_CUBE,
            TRIVIAL_COLUMN,
            TRIVIAL_COLUMN_ALT,
            TRIVIAL_COLUMN_HORIZONTAL,
            TRIVIAL_COLUMN_HORIZONTAL_ALT,
            ANVIL,
            DOOR,
            LANTERN
        }
    }
}
