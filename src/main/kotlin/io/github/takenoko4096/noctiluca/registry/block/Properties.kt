package io.github.takenoko4096.noctiluca.registry.block

import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.block.state.properties.Property
import kotlin.reflect.KClass

class Properties internal constructor(private val definitions: Set<BlockStatesConfiguration.PropertyDefinition<*>>) {
    private fun getProperty(name: String): Property<*> {
        val def = definitions.find { it.property.name == name }
        if (def == null) throw IllegalArgumentException()
        return def.property
    }

    fun boolean(name: String): BooleanProperty {
        return getProperty(name) as? BooleanProperty ?: throw IllegalArgumentException()
    }

    fun integer(name: String): IntegerProperty {
        return getProperty(name) as? IntegerProperty ?: throw IllegalArgumentException()
    }

    fun <T> enumeration(name: String, clazz: KClass<T>): EnumProperty<T> where T : Enum<T>, T : StringRepresentable {
        val property = getProperty(name) as? EnumProperty<*> ?: throw IllegalArgumentException()
        if (property.valueClass == clazz.java) {
            return property as EnumProperty<T>
        }
        else {
            throw IllegalArgumentException()
        }
    }
}