package io.github.takenoko4096.noctiluca.render.model.item.builder.condition

import io.github.takenoko4096.noctiluca.render.model.item.builder.ItemModelHandle
import io.github.takenoko4096.noctiluca.render.model.item.builder.ItemModelBuilder
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.predicates.DataComponentPredicate

abstract class Condition<C> : ItemModelHandle() {
    internal var onTrueModel: ItemModelBuilder = ItemModelBuilder {}

    internal var onFalseModel: ItemModelBuilder = ItemModelBuilder {}

    fun onTrue(callback: ItemModelBuilder.() -> Unit) {
        onTrueModel = ItemModelBuilder(callback)
    }

    fun onFalse(callback: ItemModelBuilder.() -> Unit) {
        onFalseModel = ItemModelBuilder(callback)
    }

    companion object {
        fun broken(): BrokenCondition {
            return BrokenCondition()
        }

        fun carried(): CarriedCondition {
            return CarriedCondition()
        }

        fun <C : DataComponentPredicate> component(type: DataComponentPredicate.Type<C>, value: C): ComponentCondition<C> {
            return ComponentCondition(type, value)
        }

        fun customModelData(index: Int): CustomModelDataCondition {
            return CustomModelDataCondition(index)
        }

        fun damaged(): DamagedCondition {
            return DamagedCondition()
        }

        fun extendedView(): ExtendedViewCondition {
            return ExtendedViewCondition()
        }

        fun <C : Any> hasComponent(type: DataComponentType<C>, ignoreDefault: Boolean): HasComponentCondition<C> {
            return HasComponentCondition(type, ignoreDefault)
        }

        fun usingItem(): UsingItemCondition {
            return UsingItemCondition()
        }

        fun selectedCondition(): SelectedCondition {
            return SelectedCondition()
        }

        fun viewEntity(): ViewEntityCondition {
            return ViewEntityCondition()
        }
    }
}
