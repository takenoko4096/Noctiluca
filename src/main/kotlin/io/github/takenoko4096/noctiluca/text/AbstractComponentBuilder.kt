package io.github.takenoko4096.noctiluca.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

abstract class AbstractComponentBuilder protected constructor(protected var style: Style) {
    internal abstract fun build(): Component

    protected fun copyCurrentStyle(): Style {
        val copy = Style.EMPTY
            .withColor(style.color)
            .withBold(style.isBold)
            .withItalic(style.isItalic)
            .withUnderlined(style.isUnderlined)
            .withObfuscated(style.isObfuscated)
            .withStrikethrough(style.isStrikethrough)
            .withInsertion(style.insertion)
            .withFont(style.font)
            .withClickEvent(style.clickEvent)
            .withHoverEvent(style.hoverEvent)

        return if (style.shadowColor == null) copy.withoutShadow()
        else copy.withShadowColor(style.shadowColor!!)
    }
}
