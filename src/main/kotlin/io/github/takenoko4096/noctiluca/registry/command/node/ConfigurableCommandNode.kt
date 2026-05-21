package io.github.takenoko4096.noctiluca.registry.command.node

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandBuildContext

open class ConfigurableCommandNode<S>(val registryAccess: CommandBuildContext, val name: String, builder: ConfigurableCommandNode<S>.() -> Unit) : CommandNode<S>(LiteralArgumentBuilder.literal<S>(name)) {
    init {
        builder()
    }

    fun build(): LiteralArgumentBuilder<S> {
        return argumentBuilder as LiteralArgumentBuilder<S>
    }
}
