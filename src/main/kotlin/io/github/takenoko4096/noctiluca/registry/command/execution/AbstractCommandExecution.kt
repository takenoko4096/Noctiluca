package io.github.takenoko4096.noctiluca.registry.command.execution

import com.mojang.brigadier.context.CommandContext
import io.github.takenoko4096.noctiluca.NoctilucaDsl
import kotlin.reflect.KClass

@NoctilucaDsl
abstract class AbstractCommandExecution<S>(val context: CommandContext<S>) {
    operator fun <T : Any> String.get(clazz: KClass<T>): T {
        return context.getArgument(this, clazz.java)
    }

    inline operator fun <reified T : Any> String.invoke(): T = get(T::class)
}
