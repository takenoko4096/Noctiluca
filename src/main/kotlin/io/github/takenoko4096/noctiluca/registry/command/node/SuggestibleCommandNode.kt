package io.github.takenoko4096.noctiluca.registry.command.node

import com.mojang.brigadier.Message
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.network.chat.Component

@NoctilucaDsl
class SuggestibleCommandNode<S, T>(override val argumentBuilder: RequiredArgumentBuilder<S, T>) : CommandNode<S>(argumentBuilder) {
    private var suggested = false

    fun suggests(callback: UserInputDependingSuggestionProvider<S>.() -> Unit) {
        if (suggested) throw IllegalArgumentException("block 'suggests' is duplicating; do not use suggests more than once in same block")
        suggested = true
        argumentBuilder.suggests { context, builder ->
            val p = UserInputDependingSuggestionProvider(context, builder)
            p.callback()
            p.suggestAbove()
            return@suggests p.suggestionsBuilder.buildFuture()
        }
    }

    @NoctilucaDsl
    class UserInputDependingSuggestionProvider<S> internal constructor(
        val context: CommandContext<S>,
        val suggestionsBuilder: SuggestionsBuilder
    ) {
        private val suggestions: MutableList<Suggestion<*>> = mutableListOf()

        fun suggestAbove() {
            suggestions.forEach { it.add(suggestionsBuilder) }
            suggestions.clear()
        }

        private fun string(value: String, tooltip: Message? = null) {
            suggestions.add(StringSuggestion(value, tooltip))
        }

        private fun int(value: Int, tooltip: Message? = null) {
            suggestions.add(IntSuggestion(value, tooltip))
        }

        fun strings(vararg values: String, tooltipBuilder: ((String) -> Component)? = null) {
            if (tooltipBuilder == null) {
                values.forEach(this::string)
            }
            else {
                values.forEach {
                    this.string(
                        it,
                        tooltipBuilder(it)
                    )
                }
            }
        }

        fun strings(values: List<String>, tooltipBuilder: ((String) -> Component)? = null) {
            strings(*values.toTypedArray(), tooltipBuilder = tooltipBuilder)
        }

        fun integers(vararg values: Int, tooltipBuilder: ((Int) -> Component)? = null) {
            if (tooltipBuilder == null) {
                values.forEach(this::int)
            }
            else {
                values.forEach {
                    this.int(
                        it,
                        tooltipBuilder(it)
                    )
                }
            }
        }

        fun integers(values: List<Int>, tooltipBuilder: ((Int) -> Component)? = null) {
            integers(*values.toIntArray(), tooltipBuilder = tooltipBuilder)
        }
    }

    abstract class Suggestion<T>(protected val value: T, protected val tooltip: Message?) {
        abstract fun add(suggestionsBuilder: SuggestionsBuilder)

        override fun toString(): String = value.toString()
    }

    class IntSuggestion internal constructor(value: Int, tooltip: Message?) : Suggestion<Int>(value, tooltip) {
        override fun add(suggestionsBuilder: SuggestionsBuilder) {
            if (value.toString().startsWith(suggestionsBuilder.remainingLowerCase)) {
                if (tooltip == null) {
                    suggestionsBuilder.suggest(value)
                }
                else {
                    suggestionsBuilder.suggest(value, tooltip)
                }
            }
        }
    }

    class StringSuggestion internal constructor(value: String, tooltip: Message?) : Suggestion<String>(value, tooltip) {
        override fun add(suggestionsBuilder: SuggestionsBuilder) {
            if (value.startsWith(suggestionsBuilder.remainingLowerCase)) {
                if (tooltip == null) {
                    suggestionsBuilder.suggest(value)
                }
                else {
                    suggestionsBuilder.suggest(value, tooltip)
                }
            }
        }
    }
}
