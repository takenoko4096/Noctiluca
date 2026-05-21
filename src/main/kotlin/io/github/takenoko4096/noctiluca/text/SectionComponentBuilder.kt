package io.github.takenoko4096.noctiluca.text

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.ResolvableProfile

@NoctilucaDsl
open class SectionComponentBuilder internal constructor(parent: SectionComponentBuilder?, callback: SectionComponentBuilder.() -> Unit) : AbstractComponentBuilder(parent?.copyCurrentStyle() ?: Style.EMPTY) {
    private val children = mutableListOf<AbstractComponentBuilder>()

    init {
        callback()
    }

    open fun text(text: String) {
        children.add(TextComponentBuilder(text, copyCurrentStyle()))
    }

    fun text(text: Char) {
        text(text.toString())
    }

    fun linebreak() = text('\n')

    fun space() = text(' ')

    fun translate(key: String, vararg insertions: String, fallback: String? = null) {
        children.add(TranslatableComponentBuilder(key, insertions, fallback, copyCurrentStyle()))
    }

    fun atlas(atlas: Identifier, sprite: Identifier, fallback: (SectionComponentBuilder.() -> Unit)? = null) {
        children.add(AtlasComponentBuilder(atlas, sprite, SectionComponentBuilder(this, fallback ?: {}), copyCurrentStyle()))
    }

    fun player(profile: ResolvableProfile, showHeadOverlay: Boolean = false, fallback: (SectionComponentBuilder.() -> Unit)? = null) {
        children.add(PlayerComponentBuilder(profile, showHeadOverlay, SectionComponentBuilder(this, fallback ?: {}), copyCurrentStyle()))
    }

    fun keybind(name: String) {
        children.add(KeybindComponentBuilder(name, copyCurrentStyle()))
    }

    fun component(component: Component) {
        children.add(object : AbstractComponentBuilder(copyCurrentStyle()) {
            override fun build(): MutableComponent {
                return component.copy()
            }
        })
    }

    fun bold(flag: Boolean = true) {
        style = style.withBold(flag)
    }

    fun italic(flag: Boolean = true) {
        style = style.withItalic(flag)
    }

    fun underlined(flag: Boolean = true) {
        style = style.withUnderlined(flag)
    }

    fun obfuscated(flag: Boolean = true) {
        style = style.withObfuscated(flag)
    }

    fun strikeThrough(flag: Boolean = true) {
        style = style.withStrikethrough(flag)
    }

    fun font(font: FontDescription? = null) {
        style = style.withFont(font)
    }

    fun textColor(color: RgbColor) {
        style = color.textAppliedCopyOf(style)
    }

    fun shadowColor(color: RgbColor) {
        style = color.shadowAppliedCopyOf(style)
    }

    fun section(bold: Boolean? = null, italic: Boolean? = null, underlined: Boolean? = null, obfuscated: Boolean? = null, strikeThrough: Boolean? = null, font: FontDescription? = null, textColor: RgbColor? = null, shadowColor: RgbColor? = null, callback: SectionComponentBuilder.() -> Unit) {
        children.add(SectionComponentBuilder(this) {
            if (bold != null) bold(bold)
            if (italic != null) italic(italic)
            if (underlined != null) underlined(underlined)
            if (bold != null) bold(bold)
            if (obfuscated != null) obfuscated(obfuscated)
            if (strikeThrough != null) strikeThrough(strikeThrough)
            if (font != null) font(font)
            if (textColor != null) textColor(textColor)
            if (shadowColor != null) shadowColor(shadowColor)
            callback()
        })
    }

    /*fun gradient(left: RgbColor, right: RgbColor, callback: AbstractGradientComponentBuilder.() -> Unit) {
        children.add(
            SimpleGradientComponentBuilder(
                left,
                right,
                copyCurrentStyle(),
                callback
            )
        )
    }*/

    fun gradient(vararg colors: RgbColor, callback: AbstractGradientComponentBuilder.() -> Unit) {
        children.add(
            MultiGradientComponentBuilder(
                colors.toList(),
                style,
                callback
            )
        )
    }

    fun rainbow(start: RgbColor? = null, callback: AbstractGradientComponentBuilder.() -> Unit) {
        children.add(
            RainbowGradientComponentBuilder(
                start,
                copyCurrentStyle(),
                callback
            )
        )
    }

    fun onClick(clickEvent: ClickEvent) {
        style = style.withClickEvent(clickEvent)
    }

    fun onHover(hoverEvent: HoverEvent) {
        style = style.withHoverEvent(hoverEvent)
    }

    override fun build(): MutableComponent {
        val component = Component.empty()

        for (builder in children) {
            component.append(builder.build())
        }

        return component
    }
}
