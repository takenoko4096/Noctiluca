package io.github.takenoko4096.noctiluca.registry.command.execution

import com.mojang.brigadier.context.CommandContext
import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.text.RgbColor
import io.github.takenoko4096.noctiluca.text.SectionComponentBuilder
import io.github.takenoko4096.noctiluca.text.component
import net.minecraft.commands.CommandSourceStack

@NoctilucaDsl
class ReturnableCommandExecution<S>(context: CommandContext<S>) : AbstractCommandExecution<S>(context) {
    fun CommandContext<CommandSourceStack>.successful(returns: Int = 1, callback: SectionComponentBuilder.() -> Unit): Int {
        source.sendSystemMessage(component(callback))
        return returns
    }

    fun CommandContext<CommandSourceStack>.failure(callback: SectionComponentBuilder.() -> Unit): Int {
        source.sendSystemMessage(component {
            textColor(RgbColor.RED)
            section(callback=callback)
        })
        return 0
    }
}
