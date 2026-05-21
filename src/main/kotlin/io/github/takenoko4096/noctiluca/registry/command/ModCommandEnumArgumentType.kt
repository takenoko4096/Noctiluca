package io.github.takenoko4096.noctiluca.registry.command

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import net.minecraft.util.StringRepresentable
import kotlin.reflect.KClass

abstract class ModCommandEnumArgumentType<T>(mod: NoctilucaModInitializer, name: String, clazz: KClass<T>) : ModCommandArgumentType<T>(mod, name, {
    val values = clazz.java.enumConstants

    parses {
        val s = reader.readUnquotedString()

        for (value in values) {
            if (s == value.name.lowercase()) {
                return@parses value
            }
        }

        throw exception("'$s' は列挙型 $name のメンバとして無効な値です")
    }

    suggests {
        strings(values.map { it.name.lowercase() })
    }
}) where T : StringRepresentable, T : Enum<T>
