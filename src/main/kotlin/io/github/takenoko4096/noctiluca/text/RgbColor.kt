package io.github.takenoko4096.noctiluca.text

import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import java.util.Objects

class RgbColor private constructor(private val rgb: Int?) {
    val r: Int
        get() = if (rgb != null) (rgb shr 16) and 0xFF else throw IllegalStateException("NULLの色成分は取得できません")

    val g: Int
        get() = if (rgb != null) (rgb shr 8) and 0xFF else throw IllegalStateException("NULLの色成分は取得できません")

    val b: Int
        get() = if (rgb != null) rgb and 0xFF else throw IllegalStateException("NULLの色成分は取得できません")

    internal constructor(r: Int, g: Int, b: Int): this((r shl 16) + (g shl 8) + b)

    override fun hashCode(): Int {
        return Objects.hash(rgb)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is RgbColor) rgb == other.rgb else false
    }

    internal fun textAppliedCopyOf(style: Style): Style {
        return if (rgb == null) style.withColor(null as TextColor?) else style.withColor(rgb)
    }

    internal fun shadowAppliedCopyOf(style: Style): Style {
        return if (rgb == null) style.withoutShadow() else style.withShadowColor(rgb)
    }

    override fun toString(): String {
        return if (rgb == null) "RGB(UNSET)" else "RGB($r, $g, $b)"
    }

    fun toHsv(): HsvColor {
        if (rgb == null) {
            throw IllegalStateException("RGB値がnullであるためHSVに変換できません: これはNULLをHSVに変換しようとしたことを意味します")
        }

        val r = r / 255.0
        val g = g / 255.0
        val b = b / 255.0

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min

        var h = if (min == max) {
            0.0
        }
        else if (r == max) {
            60 * (g - b) / delta
        }
        else if (g == max) {
            60 * (b - r) / delta + 120
        }
        else if (b == max) {
            60 * (r - g) / delta + 240
        }
        else {
            throw IllegalStateException("NEVER HAPPENS")
        }

        if (h < 0.0) {
            h += 360.0
        }

        if (h >= 360) {
            h -= 360
        }

        val s = if (max == 0.0) 0.0 else delta / max
        val v = max

        return HsvColor(h, s, v)
    }

    companion object {
        fun rgb(rgb: Int): RgbColor {
            return RgbColor(rgb)
        }

        val NULL = RgbColor(null)

        val RED = RgbColor(16733525)
        val BLUE = RgbColor(5592575)
        val YELLOW = RgbColor(16777045)
        val GREEN = RgbColor(5635925)
        val GOLD = RgbColor(16755200)
        val AQUA = RgbColor(5636095)
        val LIGHT_PURPLE = RgbColor(16733695)
        val GRAY = RgbColor(11184810)
        val DARK_PURPLE = RgbColor(11141290)
        val DARK_GRAY = RgbColor(5592405)
        val DARK_AQUA = RgbColor(43690)
        val DARK_RED = RgbColor(11141120)
        val DARK_BLUE = RgbColor(170)
        val DARK_GREEN = RgbColor(43520)
        val WHITE = RgbColor(16777215)
        val BLACK = RgbColor(0)

        val MATERIAL_QUARTZ = RgbColor(0xE3D4D1)
        val MATERIAL_IRON = RgbColor(0xCECACA)
        val MATERIAL_NETHERITE = RgbColor(0x443A3B)
        val MATERIAL_REDSTONE = RgbColor(0x971607)
        val MATERIAL_COPPER = RgbColor(0xB4684D)
        val MATERIAL_GOLD = RgbColor(0xDEB12D)
        val MATERIAL_DIAMOND = RgbColor(0x2CBAA8)
        val MATERIAL_EMERALD = RgbColor(0x47A036)
        val MATERIAL_LAPIS = RgbColor(0x21497B)
        val MATERIAL_AMETHYST = RgbColor(0x9A5CC6)
        val MATERIAL_RESIN = RgbColor(0xEB7114)

        val MINECOIN = RgbColor(0xDDD605)
    }
}
