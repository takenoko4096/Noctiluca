package io.github.takenoko4096.noctiluca.math

import org.joml.Quaternionf
import java.util.Objects
import kotlin.math.*

class Orientation3f(var yaw: Float, var pitch: Float, var roll: Float) : IVector<Orientation3f, Float> {
    override val isZero: Boolean
        get() = equals(Orientation3f())

    override val components: List<Float>
        get () {
            return listOf(yaw, pitch, roll)
        }

    val localCoordinateSystem: CoordinateSystem
        get() = CoordinateSystem.local(this)

    constructor() : this(0f, 0f, 0f)

    constructor(yaw: Int, pitch: Int, roll: Int) : this(yaw.toFloat(), pitch.toFloat(), roll.toFloat())

    override fun hashCode(): Int {
        return Objects.hash(yaw, pitch, roll)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is Orientation3f) return false
        return yaw == other.yaw && pitch == other.pitch && roll == other.roll
    }

    @Destructive
    override fun calculate(operator: (Float) -> Float): Orientation3f {
        yaw = operator(yaw)
        pitch = operator(pitch)
        roll = operator(roll)
        return this
    }

    @Destructive
    override fun calculate(other: Orientation3f, operator: (Float, Float) -> Float): Orientation3f {
        yaw = operator(yaw, other.yaw)
        pitch = operator(pitch, other.pitch)
        roll = operator(roll, other.roll)
        return this
    }

    @Destructive
    override fun calculate(other1: Orientation3f, other2: Orientation3f, operator: (Float, Float, Float) -> Float): Orientation3f {
        yaw = operator(yaw, other1.yaw, other2.yaw)
        pitch = operator(pitch, other1.pitch, other2.pitch)
        roll = operator(roll, other1.roll, other2.roll)
        return this
    }

    @Destructive
    override infix fun add(other: Orientation3f): Orientation3f {
        return calculate(other) { a, b -> a + b }
    }

    @Destructive
    override infix fun subtract(other: Orientation3f): Orientation3f {
        return calculate(other) { a, b -> a - b }
    }

    @Destructive
    override infix fun scale(scalar: Float): Orientation3f {
        return calculate { component -> component * scalar }
    }

    override infix fun divide(scalar: Float): Orientation3f {
        if (scalar == 0f) {
            throw IllegalArgumentException("0 で割ることはできません")
        }

        return calculate { component -> component / scalar }
    }

    override fun invert(): Orientation3f {
        return this.localCoordinateSystem.back()
    }

    @Destructive
    override fun clamp(min: Orientation3f, max: Orientation3f): Orientation3f {
        return calculate(min, max) { value, minValue, maxValue ->
            max(minValue, min(value, maxValue))
        }
    }

    override fun format(pattern: String, digits: Int): String {
        val format = "%.${digits}f"

        val sy = String.format(format, yaw)
        val sp = String.format(format, pitch)
        val sr = String.format(format, roll)

        return pattern
            .replace("#yaw".toRegex(), sy)
            .replace("#pitch".toRegex(), sp)
            .replace("#roll".toRegex(), sr)
    }

    override fun toString(): String {
        return format("(#yaw, #pitch, #roll)", 2)
    }

    override fun copy(): Orientation3f {
        return Orientation3f(yaw, pitch, roll)
    }

    fun toRotation2f(): Rotation2f {
        return Rotation2f(yaw, pitch)
    }

    fun toDirection3d(): Vector3d {
        return toRotation2f().toDirection3d()
    }

    fun toQuaternion4f(): Quaternionf {
        val quaternion = Quaternionf(0f, 0f, 0f, 1f)
        val system = localCoordinateSystem

        fun rotate(axis: Vector3d, angle: Float) {
            val normalized: Vector3d = axis.copy().normalize()
            quaternion.rotateAxis(
                (angle * PI / 180).toFloat(),
                normalized.x.toFloat(),
                normalized.y.toFloat(),
                normalized.z.toFloat()
            )
        }

        rotate(system.z, roll)
        rotate(system.x, pitch)
        rotate(Vector3d.UP, -(yaw + 90))
        return quaternion
    }

    companion object {
        fun from(other: Rotation2f): Orientation3f {
            return Orientation3f(other.yaw, other.pitch, 0f)
        }
    }
}
