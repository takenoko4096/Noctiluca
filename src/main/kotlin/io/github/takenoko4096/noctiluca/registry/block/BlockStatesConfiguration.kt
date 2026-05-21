package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.block.state.properties.Property
import kotlin.reflect.KClass

@NoctilucaDsl
class BlockStatesConfiguration internal constructor() {
    private val states = mutableSetOf<PropertyDefinition<*>>()

    fun booleanProperty(name: String, callback: PropertyConfiguration<Boolean>.() -> Unit) {
        if (states.any { it.property.name == name }) {
            throw IllegalStateException()
        }

        val config = BooleanPropertyConfiguration(name)
        config.callback()
        states.add(config.build())
    }

    fun integerProperty(name: String, callback: IntegerPropertyConfiguration.() -> Unit) {
        if (states.any { it.property.name == name }) {
            throw IllegalStateException()
        }

        val config = IntegerPropertyConfiguration(name)
        config.callback()
        states.add(config.build())
    }

    fun <T> enumerationProperty(name: String, clazz: KClass<T>, callback: EnumerationPropertyConfiguration<T>.() -> Unit) where T : Enum<T>, T : StringRepresentable {
        if (states.any { it.property.name == name }) {
            throw IllegalStateException()
        }

        val config = EnumerationPropertyConfiguration(name, clazz)
        config.callback()
        states.add(config.build())
    }

    internal fun build(): Set<PropertyDefinition<*>> {
        return states.toSet()
    }

    abstract class PropertyConfiguration<T : Comparable<T>> internal constructor(protected val name: String) {
        var defaultValue: T? = null

        abstract fun build(): PropertyDefinition<T>
    }

    class BooleanPropertyConfiguration internal constructor(name: String) : PropertyConfiguration<Boolean>(name) {
        override fun build(): PropertyDefinition<Boolean> {
            if (defaultValue == null) {
                throw IllegalStateException()
            }

            return PropertyDefinition(
                BooleanProperty.create(name),
                defaultValue!!
            )
        }
    }

    class IntegerPropertyConfiguration internal constructor(name: String) : PropertyConfiguration<Int>(name) {
        var range: IntRange? = null

        override fun build(): PropertyDefinition<Int> {
            if (defaultValue == null) {
                throw IllegalStateException()
            }
            else if (range == null) {
                throw IllegalStateException()
            }

            return PropertyDefinition(
                IntegerProperty.create(name, range!!.first, range!!.last),
                defaultValue!!
            )
        }
    }

    class EnumerationPropertyConfiguration<T> internal constructor(name: String, private val clazz: KClass<T>) : PropertyConfiguration<T>(name) where T : Enum<T>, T : StringRepresentable {
        override fun build(): PropertyDefinition<T> {
            if (defaultValue == null) {
                throw IllegalStateException()
            }

            return PropertyDefinition(
                EnumProperty.create(name, clazz.java),
                defaultValue!!
            )
        }
    }

    class PropertyDefinition<T : Comparable<T>> internal constructor(
        val property: Property<T>,
        val defaultValue: T
    ) {
        internal fun setDefaultValueTo(blockState: BlockState): BlockState {
            return blockState.setValue(property, defaultValue)
        }
    }
}
