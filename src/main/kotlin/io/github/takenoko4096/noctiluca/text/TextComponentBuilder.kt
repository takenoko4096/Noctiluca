package io.github.takenoko4096.noctiluca.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

class TextComponentBuilder internal constructor(private val text: String, style: Style) : AbstractComponentBuilder(style) {
    override fun build(): MutableComponent {
        return Component.literal(text).withStyle(style)
    }
}

