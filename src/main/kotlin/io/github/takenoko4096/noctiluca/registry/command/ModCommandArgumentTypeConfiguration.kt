package io.github.takenoko4096.noctiluca.registry.command

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.registry.command.node.SuggestibleCommandNode
import net.minecraft.util.StringRepresentable

@NoctilucaDsl
class ModCommandArgumentTypeConfiguration<T : StringRepresentable>(private val mod: NoctilucaModInitializer, private val name: String, callback: ModCommandArgumentTypeConfiguration<T>.() -> Unit) {
    internal var parser: (ArgumentParser.() -> T)? = null

    internal var suggester: (SuggestibleCommandNode.UserInputDependingSuggestionProvider<*>.() -> Unit)? = null

    init {
        callback()
    }

    fun parses(callback: ArgumentParser.() -> T) {
        if (parser != null) {
            throw IllegalStateException("HA??????????????????????? parser ${mod.identifier} $name $callback")
        }

        parser = callback
    }

    fun suggests(callback: SuggestibleCommandNode.UserInputDependingSuggestionProvider<*>.() -> Unit) {
        if (suggester != null) {
            throw IllegalStateException("HA??????????????????????? suggester ${mod.identifier} $name $callback")
        }

        suggester = callback
    }

    class ArgumentParser internal constructor(val reader: StringReader) {
        fun exception(message: String): CommandSyntaxException {
            return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().createWithContext(reader, message)
        }
    }
}
