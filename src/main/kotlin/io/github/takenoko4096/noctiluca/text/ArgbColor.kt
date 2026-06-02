package io.github.takenoko4096.noctiluca.text

class ArgbColor(val a: Int, val rgb: RgbColor) {
    val r: Int
        get() = rgb.r

    val g: Int
        get() = rgb.g

    val b: Int
        get() = rgb.b

    val argbValue
        get() = ((a and 0xFF) shl 24) or (rgb.rgbValue and 0xFFFFFF)
}
