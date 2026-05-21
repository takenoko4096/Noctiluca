package io.github.takenoko4096.noctiluca.registry.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.registry.StarlightRegistry
import io.github.takenoko4096.noctiluca.registry.command.node.ConfigurableCommandNode
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.util.StringRepresentable

class ModCommandRegistry(public override val mod: NoctilucaModInitializer) : StarlightRegistry(mod) {
    private val commands = mutableSetOf<(CommandBuildContext) -> LiteralArgumentBuilder<CommandSourceStack>>()

    private val argumentTypes = mutableMapOf<String, ModCommandArgumentType<*>>()

    init {
        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            commands.forEach {
                val command = it(registryAccess)
                dispatcher.register(command)
            }
        }
    }

    fun register(name: String, callback: ConfigurableCommandNode<CommandSourceStack>.() -> Unit) {
        commands.add {
            ConfigurableCommandNode(it, name, callback).build()
        }
    }

    fun <T: StringRepresentable, U : ModCommandArgumentType<T>> registerArgumentTypeByConfiguredSingleton(type: U) {
        if (type.identifier.path in argumentTypes) {
            throw IllegalArgumentException("argument type ${type.identifier} is already registered!")
        }
        else if (argumentTypes.values.any { it::class.java == type::class.java }) {
            throw IllegalArgumentException(
                "DO NOT MAKE THE ARGUMENT TYPE REGISTRATION LOGIC DYNAMIC!!!!!!!!!! All classes are always generated statically, depending on location in the source code, with no exceptions. Fabric requires class object which has independent class signature to register argument types, so you cannot dynamically register the argument type"
            )
        }

        ArgumentTypeRegistry.registerArgumentType(
            type.identifier,
            type::class.java,
            SingletonArgumentInfo.contextFree { type }
        )

        argumentTypes[type.identifier.path] = type
    }

    inline fun <reified T : StringRepresentable> registerArgumentType(name: String, noinline configuration: ModCommandArgumentTypeConfiguration<T>.() -> Unit): ModCommandArgumentType<T> {
        val type = object : ModCommandArgumentType<T>(mod, name, configuration) {}
        registerArgumentTypeByConfiguredSingleton(type)
        return type
    }

    inline fun <reified T> registerEnumArgumentType(name: String): ModCommandEnumArgumentType<T> where T : StringRepresentable, T : Enum<T> {
        val type = object : ModCommandEnumArgumentType<T>(mod, name, T::class) {}
        registerArgumentTypeByConfiguredSingleton(type)
        return type
    }
}
