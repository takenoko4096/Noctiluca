package io.github.takenoko4096.noctiluca.text

import kotlin.math.floor

class HsvColor(val h: Double, val s: Double, val v: Double) {
    fun toRgb(): RgbColor {
        if (s == 0.0) {
            val value = (v * 255).toInt()
            return RgbColor(value, value, value)
        }

        val normal = (h % 360 + 360) % 360
        val prime = normal / 60.0
        val i = floor(prime).toInt()
        val f = prime - i

        val p = v * (1 - s)
        val q = v * (1 - s * f)
        val t = v * (1 - s * (1 - f))

        val r: Double
        val g: Double
        val b: Double

        when (i) {
            0 -> {
                r = v; g = t; b = p
            }
            1 -> {
                r = q; g = v; b = p
            }
            2 -> {
                r = p; g = v; b = t
            }
            3 -> {
                r = p; g = q; b = v
            }
            4 -> {
                r = t; g = p; b = v
            }
            5 -> {
                r = v; g = p; b = q
            }
            else -> throw IllegalStateException("NEVER HAPPENS")
        }

        return RgbColor(
            (r * 255).toInt(),
            (g * 255).toInt(),
            (b * 255).toInt()
        )
    }

    override fun toString(): String {
        return "HSV($h, $s, $v)"
    }
}
