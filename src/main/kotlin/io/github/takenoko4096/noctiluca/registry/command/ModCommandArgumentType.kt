package io.github.takenoko4096.noctiluca.registry.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.registry.command.ModCommandArgumentTypeConfiguration.ArgumentParser
import io.github.takenoko4096.noctiluca.registry.command.node.SuggestibleCommandNode
import net.minecraft.util.StringRepresentable
import java.util.concurrent.CompletableFuture

abstract class ModCommandArgumentType<T : StringRepresentable>(mod: NoctilucaModInitializer, name: String, callback: ModCommandArgumentTypeConfiguration<T>.() -> Unit) : ArgumentType<T> {
    private val parser: ArgumentParser.() -> T
    private val suggester: SuggestibleCommandNode.UserInputDependingSuggestionProvider<*>.() -> Unit

    val identifier = mod.identifierOf(name)

    init {
        val c = ModCommandArgumentTypeConfiguration(mod, name, callback)

        if (c.parser == null) {
            throw IllegalStateException("'parses' is not specified")
        }

        if (c.suggester == null) {
            throw IllegalStateException("'suggests' is not specified")
        }

        parser = c.parser!!
        suggester = c.suggester!!
    }

    final override fun parse(reader: StringReader): T {
        return ArgumentParser(reader).parser()
    }

    final override fun <S> listSuggestions(context: CommandContext<S>, suggestionsBuilder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val s = SuggestibleCommandNode.UserInputDependingSuggestionProvider(context, suggestionsBuilder)
        s.suggester()
        s.suggestAbove()
        return suggestionsBuilder.buildFuture()
    }
}
