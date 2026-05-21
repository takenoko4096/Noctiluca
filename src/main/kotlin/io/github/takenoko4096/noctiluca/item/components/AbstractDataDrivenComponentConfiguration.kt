package io.github.takenoko4096.noctiluca.item.components

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.item.ItemComponent
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentType

abstract class AbstractDataDrivenComponentConfiguration<T : Any>(mod: NoctilucaModInitializer, protected val dataSource: HolderLookup.Provider, type: DataComponentType<T>) : AbstractComponentConfiguration<T>(mod, type) {
    override fun toComponent(): ItemComponent<T> {
        return ItemComponent.valued(type, build())
    }

    abstract override fun build(): T
}
