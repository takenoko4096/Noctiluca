package io.github.takenoko4096.noctiluca.math

import net.minecraft.world.phys.Vec2
import java.util.Objects
import kotlin.math.*

class Rotation2f(var yaw: Float, var pitch: Float) : IVector<Rotation2f, Float> {
    override val isZero
        get() = equals(Rotation2f())

    override val components: List<Float>
        get() {
            return listOf(yaw, pitch)
        }

    constructor() : this(0f, 0f)

    constructor(yaw: Int, pitch: Int) : this(yaw.toFloat(), pitch.toFloat())

    override fun hashCode(): Int {
        return Objects.hash(yaw, pitch)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is Rotation2f) return false
        return yaw == other.yaw && pitch == other.pitch
    }

    @Destructive
    override fun calculate(operator: (Float) -> Float): Rotation2f {
        yaw = operator(yaw)
        pitch = operator(pitch)
        return this
    }

    @Destructive
    override fun calculate(other: Rotation2f, operator: (Float, Float) -> Float): Rotation2f {
        yaw = operator(yaw, other.yaw)
        pitch = operator(pitch, other.pitch)
        return this
    }

    @Destructive
    override fun calculate(other1: Rotation2f, other2: Rotation2f, operator: (Float, Float, Float) -> Float): Rotation2f {
        this.yaw = operator(yaw, other1.yaw, other2.yaw)
        this.pitch = operator(pitch, other1.pitch, other2.pitch)
        return this
    }

    @Destructive
    override infix fun add(other: Rotation2f): Rotation2f {
        return calculate(other) { a, b -> a + b }
    }

    @Destructive
    override infix fun subtract(other: Rotation2f): Rotation2f {
        return calculate(other) { a, b -> a - b }
    }

    @Destructive
    override infix fun scale(scalar: Float): Rotation2f {
        return calculate { component -> component * scalar }
    }

    override infix fun divide(scalar: Float): Rotation2f {
        if (scalar == 0f) {
            throw IllegalArgumentException("0 で割ることはできません")
        }

        return calculate { component -> component / scalar }
    }

    @Destructive
    override fun invert(): Rotation2f {
        yaw += 180f
        pitch *= -1f
        return this
    }

    @Destructive
    override fun clamp(min: Rotation2f, max: Rotation2f): Rotation2f {
        return calculate(min, max) { value, minValue, maxValue ->
            max(minValue, min(value, maxValue))
        }
    }

    override fun format(pattern: String, digits: Int): String {
        val format = "%.${digits}f"

        val sy = String.format(format, yaw)
        val sp = String.format(format, pitch)

        return pattern
            .replace("#yaw".toRegex(), sy)
            .replace("#pitch".toRegex(), sp)
    }

    override fun toString(): String {
        return format("(#yaw, #pitch)", 2)
    }

    override fun copy(): Rotation2f {
        return Rotation2f(yaw, pitch)
    }

    infix fun angleBetween(other: Rotation2f): Double {
        return toDirection3d().angleBetween(other.toDirection3d())
    }

    fun toDirection3d(): Vector3d {
        return Vector3d(
            -sin(yaw * PI / 180) * cos(pitch * PI / 180),
            -sin(pitch * PI / 180),
            cos(yaw * PI / 180) * cos(pitch * PI / 180)
        )
    }

    fun toVec2(): Vec2 {
        return Vec2(pitch, yaw)
    }

    companion object {
        val NORTH
            get() = Rotation2f(180f, 0f)

        val SOUTH
            get() = Rotation2f(0f, 0f)

        val EAST
            get() = Rotation2f(-90f, 0f)

        val WEST
            get() = Rotation2f(90f, 0f)

        val UP
            get() = Rotation2f(0f, -90f)

        val DOWN
            get() = Rotation2f(0f, 90f)

        fun from(vec2: Vec2): Rotation2f {
            return Rotation2f(vec2.y, vec2.x)
        }
    }
}
