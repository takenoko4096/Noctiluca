package io.github.takenoko4096.noctiluca.item.components

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.item.ItemComponent
import net.minecraft.core.component.DataComponentType

abstract class AbstractComponentConfiguration<T : Any>(protected val mod: NoctilucaModInitializer, protected val type: DataComponentType<T>) {
    internal open fun toComponent(): ItemComponent<T> {
        return ItemComponent.valued(type, build())
    }

    protected abstract fun build(): T
}
