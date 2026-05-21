package io.github.takenoko4096.noctiluca.text

import net.minecraft.network.chat.Style
import kotlin.math.floor
import kotlin.math.min

class MultiGradientComponentBuilder internal constructor(private val colors: List<RgbColor>, style: Style, callback: MultiGradientComponentBuilder.() -> Unit) : AbstractGradientComponentBuilder(style) {
    init {
        if (colors.isEmpty()) {
            throw IllegalArgumentException("色のリストが空のためグラデーションを決定できません")
        }

        callback()
    }

    override fun color(t: Double): RgbColor {
        if (colors.size == 1) return colors[0]

        val segment = t * (colors.size - 1)
        val i = floor(segment).toInt()
        val local = segment - i

        val l = colors[i.coerceIn(0..<colors.size)].toHsv()
        val r = colors[min(i + 1, colors.size - 1)].toHsv()

        return lerp(l, r, local).toRgb()
    }
}
