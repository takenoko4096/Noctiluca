package io.github.takenoko4096.noctiluca.item

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.util.Unit
import net.minecraft.world.item.Item
import java.util.Objects

abstract class ItemComponent<T : Any> protected constructor(type: DataComponentType<T>, protected val value: T) : io.github.takenoko4096.noctiluca.item.AbstractItemComponent<T>(type) {
    override fun set(target: Item.Properties) {
        target.component(type, value)
    }

    override fun set(target: DataComponentPatch.Builder) {
        target.set(type, value)
    }

    override fun hashCode(): Int {
        return Objects.hash(type, value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemComponent<*>) return false
        return type == other.type && value == other.value
    }

    open class Valued<T : Any> internal constructor(type: DataComponentType<T>, value: T) : ItemComponent<T>(type, value)

    class NonValued internal constructor(type: DataComponentType<Unit>) : ItemComponent<Unit>(type, Unit.INSTANCE)

    class Negative<T : Any> internal constructor(type: DataComponentType<T>) : io.github.takenoko4096.noctiluca.item.AbstractItemComponent<T>(type) {
        override fun set(target: Item.Properties) {

        }

        override fun set(target: DataComponentPatch.Builder) {
            target.remove(type)
        }
    }

    companion object {
        fun <T : Any> valued(type: DataComponentType<T>, value: T): Valued<T> {
            return Valued(type, value)
        }

        fun nonValued(type: DataComponentType<Unit>): NonValued {
            return NonValued(type)
        }

        fun <T : Any> negative(type: DataComponentType<T>): Negative<T> {
            return Negative(type)
        }
    }
}
