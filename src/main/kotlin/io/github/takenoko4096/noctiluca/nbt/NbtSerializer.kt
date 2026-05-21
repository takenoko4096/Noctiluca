package io.github.takenoko4096.noctiluca.nbt

import io.github.takenoko4096.mojangson.values.MojangsonByteArray
import io.github.takenoko4096.mojangson.values.MojangsonCompound
import io.github.takenoko4096.mojangson.values.MojangsonIntArray
import io.github.takenoko4096.mojangson.values.MojangsonIterable
import io.github.takenoko4096.mojangson.values.MojangsonLongArray
import io.github.takenoko4096.mojangson.values.MojangsonPrimitive
import io.github.takenoko4096.mojangson.values.MojangsonStructure
import io.github.takenoko4096.noctiluca.text.RgbColor
import io.github.takenoko4096.noctiluca.text.component
import net.minecraft.network.chat.Component
import kotlin.charArrayOf
import kotlin.reflect.KClass
import kotlin.text.contains
import kotlin.text.repeat

class NbtSerializer private constructor(private val value: MojangsonStructure, private val indentationSpaceCount: Int) {
    private fun serialize(): Component {
        return serialize(value, 1)
    }

    private fun serialize(value: Any?, indentation: Int): Component {
        return when (value) {
            is Number -> number(value)
            is String -> string(value)
            is MojangsonCompound -> compound(value, indentation)
            is MojangsonIterable<*> -> iterable(value, indentation)
            is MojangsonPrimitive<*> -> serialize(value.value, indentation)
            null -> `null`()
            else -> throw NbtSerializationException("このオブジェクトは無効な型の値を含みます: $value (${value::class.qualifiedName})")
        }
    }

    private fun compound(compound: MojangsonCompound, indentation: Int): Component {
        val keys = compound.keys().toTypedArray()
        return component {
            textColor(RgbColor.WHITE)
            text(COMPOUND_BRACES[0])

            section {
                for ((i, key) in keys.withIndex()) {
                    try {
                        val childValue: Any = compound.get(key, compound.getTypeOf(key))

                        text(LINE_BREAK)
                        text(indentation(indentation + 1))

                        section {
                            textColor(RgbColor.AQUA)
                            component(key(key))
                        }

                        text(COLON)
                        text(WHITESPACE)
                        component(serialize(childValue, indentation + 1))
                    }
                    catch (e: IllegalArgumentException) {
                        throw NbtSerializationException(
                            "キー '$key' における無効な型: ${compound.getTypeOf(key)}",
                            e
                        )
                    }

                    if (i != keys.size - 1) {
                        text(COMMA)
                    }
                }

                if (keys.isNotEmpty()) {
                    text(LINE_BREAK)
                    text(indentation(indentation))
                }
            }

            text(COMPOUND_BRACES[1])
        }
    }

    private fun iterable(iterable: MojangsonIterable<*>, indentation: Int): Component {
        return component {
            textColor(RgbColor.WHITE)
            text(ARRAY_LIST_BRACES[0])

            section {
                if (ITERABLE_TYPE_SYMBOLS.containsKey(iterable::class)) {
                    section {
                        textColor(RgbColor.RED)
                        text(ITERABLE_TYPE_SYMBOLS[iterable::class]!!)
                    }
                    text(SEMICOLON)
                }

                for ((i, element) in iterable.withIndex()) {
                    if (i >= 1) {
                        text(COMMA)
                    }

                    try {
                        text(LINE_BREAK)
                        text(indentation(indentation + 1))
                        component(serialize(element, indentation + 1))
                    }
                    catch (e: IllegalArgumentException) {
                        throw NbtSerializationException(
                            "インデックス '$i' における無効な型: ${element.javaClass.name}", e
                        )
                    }
                }

                if (!iterable.isEmpty) {
                    text(LINE_BREAK)
                    text(indentation(indentation))
                }
            }

            text(ARRAY_LIST_BRACES[1])
        }
    }

    private fun key(value: String): Component {
        return quotable(value, RgbColor.AQUA)
    }

    private fun string(value: String): Component {
        return quotable(value, RgbColor.GREEN)
    }

    private fun quotable(value: String, color: RgbColor): Component {
        val quote: Char?
        val string: String

        if (value.contains(DOUBLE_QUOTE) && !value.contains(SINGLE_QUOTE)) {
            quote = SINGLE_QUOTE
            string = value
        }
        else if (!value.contains(DOUBLE_QUOTE) && value.contains(SINGLE_QUOTE)) {
            quote = DOUBLE_QUOTE
            string = value
        }
        else if (value.contains(DOUBLE_QUOTE) && value.contains(SINGLE_QUOTE)) {
            quote = DOUBLE_QUOTE
            string = value.replace(DOUBLE_QUOTE.toString(), ESCAPE.toString() + DOUBLE_QUOTE)
        }
        else if (SYMBOLS_ON_STRING.any { value.contains(it) }) {
            quote = DOUBLE_QUOTE
            string = value
        }
        else {
            quote = null
            string = value
        }

        return component {
            if (quote != null) {
                text(quote)
            }

            section {
                textColor(color)
                text(string)
            }

            if (quote != null) {
                text(quote)
            }
        }
    }

    private fun number(value: Number): Component {
        return component {
            textColor(RgbColor.GOLD)
            text(value.toString())

            if (NUMBER_TYPE_SYMBOLS.contains(value::class)) {
                textColor(RgbColor.RED)
                text(NUMBER_TYPE_SYMBOLS[value::class]!!)
            }
        }
    }

    private fun `null`(): Component {
        return component {
            textColor(RgbColor.LIGHT_PURPLE)
            text("null")
        }
    }

    private fun indentation(indentation: Int): String {
        return WHITESPACE.toString().repeat(indentationSpaceCount).repeat(indentation - 1)
    }

    companion object {
        private const val LINE_BREAK = '\n'
        private const val SINGLE_QUOTE = '\''
        private const val DOUBLE_QUOTE = '"'
        private const val COLON = ':'
        private const val COMMA = ','
        private const val SEMICOLON = ';'
        private val COMPOUND_BRACES = charArrayOf('{', '}')
        private val ARRAY_LIST_BRACES = charArrayOf('[', ']')
        private const val WHITESPACE = ' '
        private const val ESCAPE = '\\'

        private val SYMBOLS_ON_STRING = setOf(
            COLON, COMMA, SEMICOLON,
            COMPOUND_BRACES[0], COMPOUND_BRACES[1],
            ARRAY_LIST_BRACES[0], ARRAY_LIST_BRACES[1],
            WHITESPACE, LINE_BREAK, ESCAPE
        )

        private val ITERABLE_TYPE_SYMBOLS = mapOf<KClass<out MojangsonIterable<*>>, Char>(
            MojangsonByteArray::class to 'B',
            MojangsonIntArray::class to 'I',
            MojangsonLongArray::class to 'L'
        )

        private val NUMBER_TYPE_SYMBOLS = mapOf<KClass<out Number>, Char>(
            Byte::class to 'b',
            Short::class to 's',
            Long::class to 'L',
            Float::class to 'f',
            Double::class to 'd'
        )

        fun serialize(value: MojangsonStructure): Component {
            return NbtSerializer(value, 4).serialize()
        }
    }
}
