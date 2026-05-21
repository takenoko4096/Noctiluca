package io.github.takenoko4096.noctiluca.render.model.item.builder.select

import com.ibm.icu.util.TimeZone
import com.ibm.icu.util.ULocale
import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.render.model.item.builder.ItemModelHandle
import io.github.takenoko4096.noctiluca.render.model.item.builder.ItemModelBuilder
import net.minecraft.core.component.DataComponentType
import net.minecraft.world.level.block.state.properties.Property

@NoctilucaDsl
abstract class Select<C> : ItemModelHandle() {
    internal var cases = SelectCases<C> {}

    internal var fallback: ItemModelBuilder = ItemModelBuilder {}

    fun cases(callback: SelectCases<C>.() -> Unit) {
        cases = SelectCases(callback)
    }

    fun fallback(callback: ItemModelBuilder.() -> Unit) {
        fallback = ItemModelBuilder(callback)
    }

    @NoctilucaDsl
    class SelectCases<C>(callback: SelectCases<C>.() -> Unit) {
        internal val cases = mutableListOf<SelectCase<C>>()

        init {
            callback()
        }

        fun `when`(vararg values: C, callback: ItemModelBuilder.() -> Unit) {
            cases.add(SelectCase(values.toSet(), ItemModelBuilder(callback)))
        }
    }

    companion object {
        fun <T : Comparable<T>> blockState(property: Property<T>): BlockStateSelect<T> {
            return BlockStateSelect(property)
        }

        fun chargeType(): ChargeTypeSelect {
            return ChargeTypeSelect()
        }

        fun <T : Any> component(componentType: DataComponentType<T>): ComponentSelect<T> {
            return ComponentSelect(componentType)
        }

        fun contextDimension(): ContextDimensionSelect {
            return ContextDimensionSelect()
        }

        fun contextEntityType(): ContextEntityTypeSelect {
            return ContextEntityTypeSelect()
        }

        fun customModelData(index: Int): CustomModelDataSelect {
            return CustomModelDataSelect(index)
        }

        fun displayContext(): DisplayContextSelect {
            return DisplayContextSelect()
        }

        fun localTime(format: String, locale: ULocale, timeZone: TimeZone): LocalTimeSelect {
            return LocalTimeSelect(format, locale, timeZone)
        }

        fun mainHand(): MainHandSelect {
            return MainHandSelect()
        }

        fun trimMaterial(): TrimMaterialSelect {
            return TrimMaterialSelect()
        }
    }
}
