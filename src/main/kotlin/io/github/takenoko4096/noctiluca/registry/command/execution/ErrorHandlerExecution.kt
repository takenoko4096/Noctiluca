package io.github.takenoko4096.noctiluca.registry.command.execution

import com.mojang.brigadier.context.CommandContext
import io.github.takenoko4096.noctiluca.text.RgbColor
import io.github.takenoko4096.noctiluca.text.SectionComponentBuilder
import io.github.takenoko4096.noctiluca.text.component
import net.minecraft.commands.CommandSourceStack

class ErrorHandlerExecution<S>(context: CommandContext<S>, val error: Throwable) : AbstractCommandExecution<S>(context) {
    fun CommandContext<CommandSourceStack>.sendErrorMessage(callback: SectionComponentBuilder.() -> Unit) {
        source.sendSystemMessage(component {
            textColor(RgbColor.RED)
            section(callback=callback)
        })
    }
}
