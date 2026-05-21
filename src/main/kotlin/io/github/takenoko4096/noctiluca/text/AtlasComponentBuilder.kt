package io.github.takenoko4096.noctiluca.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.contents.objects.AtlasSprite
import net.minecraft.resources.Identifier

class AtlasComponentBuilder internal constructor(private val atlas: Identifier, private val sprite: Identifier, fallback: SectionComponentBuilder, style: Style) : ObjectComponentBuilder(fallback, style) {
    override fun build(): MutableComponent {
        return Component.`object`(AtlasSprite(atlas, sprite), fallback.build()).withStyle(style)
    }
}
