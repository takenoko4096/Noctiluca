package io.github.takenoko4096.noctiluca.item

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.world.item.Item

abstract class AbstractItemComponent<T : Any>(protected val type: DataComponentType<T>) {
    abstract fun set(target: Item.Properties)

    abstract fun set(target: DataComponentPatch.Builder)
}
