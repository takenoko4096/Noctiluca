package io.github.takenoko4096.noctiluca.text

import net.minecraft.network.chat.Style

class RainbowGradientComponentBuilder internal constructor(private val start: RgbColor?, style: Style, callback: RainbowGradientComponentBuilder.() -> Unit) : AbstractGradientComponentBuilder(style) {
    init {
        callback()
    }

    override fun color(t: Double): RgbColor {
        val offset = start?.toHsv()?.h ?: 0.0

        val h = (360.0 * t + offset) % 360
        return HsvColor(h, 0.8, 1.0).toRgb()
    }
}
