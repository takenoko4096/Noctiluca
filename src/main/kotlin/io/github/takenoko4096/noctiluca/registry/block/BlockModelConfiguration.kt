package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.render.model.block.NonClientBlockModelVariant
import io.github.takenoko4096.noctiluca.render.model.block.PropertyVariants
import io.github.takenoko4096.noctiluca.render.model.block.PropertyVariants0
import io.github.takenoko4096.noctiluca.render.model.block.PropertyVariants1
import io.github.takenoko4096.noctiluca.render.model.block.PropertyVariants2
import net.minecraft.world.level.block.state.properties.Property
import org.jetbrains.annotations.ApiStatus

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