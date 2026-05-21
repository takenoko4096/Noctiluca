package io.github.takenoko4096.noctiluca.text

import net.minecraft.network.chat.Style

class SimpleGradientComponentBuilder internal constructor(private val left: RgbColor, private val right: RgbColor, style: Style, callback: SimpleGradientComponentBuilder.() -> Unit) : AbstractGradientComponentBuilder(style) {
    init {
        callback()
    }

    override fun color(t: Double): RgbColor {
        return lerp(left.toHsv(), right.toHsv(), t).toRgb()
    }
}
