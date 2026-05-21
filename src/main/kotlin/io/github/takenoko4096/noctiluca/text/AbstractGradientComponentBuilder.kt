package io.github.takenoko4096.noctiluca.text

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import net.minecraft.network.chat.Style
import java.lang.Integer.max

@NoctilucaDsl
abstract class AbstractGradientComponentBuilder protected constructor(style: Style) : AbstractComponentBuilder(style) {
    private var chars = mutableListOf<Char>()

    private val decorations = mutableMapOf<Int, Style>()

    fun text(text: String) {
        for (char in text) {
            text(char)
        }
    }

    fun text(text: Char) {
        chars.add(text)
    }

    fun space() = text(' ')

    fun linebreak() = text('\n')

    private fun decoration(callback: Style.() -> Style) {
        val previousAtHere = decorations[chars.size]
        val previousBeforeHere = decorations.keys.reduceOrNull(::max)?.let { decorations[it] }

        val previous = previousAtHere ?: previousBeforeHere ?: Style.EMPTY
        val new = previous.callback()
        decorations[chars.size] = new
    }

    fun bold(flag: Boolean = true) {
        decoration { withBold(flag) }
    }

    fun italic(flag: Boolean = true) {
        decoration { withItalic(flag) }
    }

    fun underlined(flag: Boolean = true) {
        decoration { withUnderlined(flag) }
    }

    fun obfuscated(flag: Boolean = true) {
        decoration { withObfuscated(flag) }
    }

    fun strikeThrough(flag: Boolean = true) {
        decoration { withStrikethrough(flag) }
    }

    fun font(font: FontDescription? = null) {
        decoration { withFont(font) }
    }

    protected fun lerp(l: HsvColor, r: HsvColor, t: Double): HsvColor {
        var delta = r.h - l.h
        if (delta > 180) delta -= 360
        if (delta < -180) delta += 360

        val h = (l.h + delta * t + 360) % 360
        val s = l.s + (r.s - l.s) * t
        val v = l.v + (r.v - l.v) * t

        return HsvColor(h, s, v)
    }

    protected abstract fun color(t: Double): RgbColor

    override fun build(): Component {
        if (chars.isEmpty()) {
            return Component.empty()
        }
        else if (chars.size == 1) {
            Component.literal(chars[0].toString()).withStyle(color(0.0).textAppliedCopyOf(copyCurrentStyle()))
        }

        val root = Component.empty().withStyle(copyCurrentStyle())

        val ignoredCount = chars.count(IGNORED_CHARS::contains)
        val n = (chars.size - 1 - ignoredCount)

        var style0 = copyCurrentStyle()

        for ((i, char) in chars.withIndex()) {
            val t = (i.toDouble() / n).coerceIn(0.0..1.0)
            val color = color(t)

            decorations[i]?.run {
                style0 = applyTo(style0)
            }

            root.append(
                Component.literal(char.toString())
                    .withStyle(color.textAppliedCopyOf(style0))
            )
        }

        return root
    }

    companion object {
        val IGNORED_CHARS = charArrayOf(' ', '\n', '\t', '\r')
    }
}
