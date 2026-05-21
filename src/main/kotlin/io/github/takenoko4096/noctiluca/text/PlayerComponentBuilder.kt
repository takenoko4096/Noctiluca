package io.github.takenoko4096.noctiluca.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.contents.objects.PlayerSprite
import net.minecraft.world.item.component.ResolvableProfile

class PlayerComponentBuilder internal constructor(private val profile: ResolvableProfile, private val hat: Boolean, fallback: SectionComponentBuilder, style: Style) : ObjectComponentBuilder(fallback, style) {
    override fun build(): MutableComponent {
        return Component.`object`(PlayerSprite(profile, hat), fallback.build()).withStyle(style)
    }
}
