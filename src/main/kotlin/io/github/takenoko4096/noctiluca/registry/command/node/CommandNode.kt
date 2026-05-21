package io.github.takenoko4096.noctiluca.registry.command.node

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import io.github.takenoko4096.noctiluca.registry.command.execution.AssignableCommandExecution
import io.github.takenoko4096.noctiluca.registry.command.execution.ErrorHandlerExecution
import io.github.takenoko4096.noctiluca.registry.command.execution.ReturnableCommandExecution

open class CommandNode<S> internal constructor(protected open val argumentBuilder: ArgumentBuilder<S, *>) {
    private var executed: Boolean = false

    private var catcher: (ErrorHandlerExecution<S>.() -> Unit)? = null

    private val identifiers = mutableSetOf<String>()

    private fun literal(name: String): LiteralArgumentBuilder<S> {
        return LiteralArgumentBuilder.literal(name)
    }

    private fun <T> argument(name: String, type: ArgumentType<T>): RequiredArgumentBuilder<S, T> {
        return RequiredArgumentBuilder.argument(name, type)
    }

    operator fun String.invoke(builder: CommandNode<S>.() -> Unit) {
        if (identifiers.contains(this)) {
            throw IllegalArgumentException("block '$this' is duplicating; do not use $this more than once in same block")
        }

        val subCommand = literal(this)
        val child = CommandNode(subCommand)
        child.builder()
        argumentBuilder.then(subCommand)
        identifiers.add(this)
    }

    operator fun <T> String.invoke(type: ArgumentType<T>, builder: SuggestibleCommandNode<S, T>.() -> Unit) {
        if (identifiers.contains(this)) {
            throw IllegalArgumentException("block '$this' is duplicating; do not use $this more than once in same block")
        }

        val argument = argument(this, type)
        val child = SuggestibleCommandNode(argument)
        child.builder()
        argumentBuilder.then(argument)
        identifiers.add(this)
    }

    fun requires(predicate: S.() -> Boolean) {
        argumentBuilder.requires(predicate)
    }

    fun executes(callback: AssignableCommandExecution<S>.() -> Unit) {
        if (executed) {
            throw IllegalArgumentException("block 'executes' is duplicating; do not use executes more than once in same block")
        }

        executed = true
        argumentBuilder.executes {
            val execution = AssignableCommandExecution(it)
            try {
                execution.callback()
            }
            catch (error: Throwable) {
                catcher?.invoke(ErrorHandlerExecution(it, error))
                execution.returns = 0
            }
            return@executes execution.returns
        }
    }

    fun catches(callback: ErrorHandlerExecution<S>.() -> Unit) {
        catcher = callback
    }

    fun returns(callback: ReturnableCommandExecution<S>.() -> Int) {
        if (executed) {
            throw IllegalArgumentException("block 'executes' is duplicating; do not use executes more than once in same block")
        }

        executed = true
        argumentBuilder.executes {
            val execution = ReturnableCommandExecution(it)
            val result: Int = try {
                execution.callback()
            }
            catch (error: Throwable) {
                catcher?.invoke(ErrorHandlerExecution(it, error))
                0
            }
            return@executes result
        }
    }

    fun boolean(): BoolArgumentType {
        return BoolArgumentType.bool()
    }

    fun integer(): IntegerArgumentType {
        return IntegerArgumentType.integer()
    }

    fun integer(min: Int): IntegerArgumentType {
        return IntegerArgumentType.integer(min)
    }

    fun integer(range: IntRange): IntegerArgumentType {
        return IntegerArgumentType.integer(range.min(), range.max())
    }

    fun long(): LongArgumentType {
        return LongArgumentType.longArg()
    }

    fun long(min: Long): LongArgumentType {
        return LongArgumentType.longArg(min)
    }

    fun long(range: LongRange): LongArgumentType {
        return LongArgumentType.longArg(range.min(), range.max())
    }

    fun float(): FloatArgumentType {
        return FloatArgumentType.floatArg()
    }

    fun float(min: Float): FloatArgumentType {
        return FloatArgumentType.floatArg(min)
    }

    fun float(min: Float, max: Float): FloatArgumentType {
        return FloatArgumentType.floatArg(min, max)
    }

    fun double(): DoubleArgumentType {
        return DoubleArgumentType.doubleArg()
    }

    fun double(min: Double): DoubleArgumentType {
        return DoubleArgumentType.doubleArg(min)
    }

    fun double(min: Double, max: Double): DoubleArgumentType {
        return DoubleArgumentType.doubleArg(min, max)
    }

    fun string(): StringArgumentType {
        return StringArgumentType.string()
    }

    fun word(): StringArgumentType {
        return StringArgumentType.word()
    }

    fun greedyString(): StringArgumentType {
        return StringArgumentType.greedyString()
    }
}