package io.github.takenoko4096.noctiluca.math

import net.minecraft.world.phys.Vec3
import java.util.Objects
import kotlin.math.*

class Vector3d(var x: Double, var y: Double, var z: Double) : IVector<Vector3d, Double> {
    override val isZero
        get() = equals(Vector3d())

    override val components: List<Double>
        get() {
            return listOf(x, y, z)
        }

    @Destructive
    var length
        get() = sqrt(dot(this))
        set(value) {
            normalize().scale(value)
        }

    constructor() : this(0.0, 0.0, 0.0)

    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())

    override fun hashCode(): Int {
        return Objects.hash(x, y, z)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (other !is Vector3d) return false
        return x == other.x && y == other.y && z == other.z
    }

    @Destructive
    override fun calculate(operator: (Double) -> Double): Vector3d {
        x = operator(x)
        y = operator(y)
        z = operator(z)
        return this
    }

    @Destructive
    override fun calculate(other: Vector3d, operator: (Double, Double) -> Double): Vector3d {
        x = operator(x, other.x)
        y = operator(y, other.y)
        z = operator(z, other.z)
        return this
    }

    override fun calculate(other1: Vector3d, other2: Vector3d, operator: (Double, Double, Double) -> Double): Vector3d {
        x = operator(x, other1.x, other2.x)
        y = operator(y, other1.y, other2.y)
        z = operator(z, other1.z, other2.z)
        return this
    }

    @Destructive
    override infix fun add(other: Vector3d): Vector3d {
        return calculate(other) { a, b -> a + b }
    }

    @Destructive
    override infix fun subtract(other: Vector3d): Vector3d {
        return calculate(other) { a, b -> a - b }
    }

    @Destructive
    override infix fun scale(scalar: Double): Vector3d {
        return calculate { component -> component * scalar }
    }

    override infix fun divide(scalar: Double): Vector3d {
        if (scalar == 0.0) {
            throw IllegalArgumentException("0 で割ることはできません")
        }

        return calculate { component -> component / scalar }
    }

    @Destructive
    override fun invert(): Vector3d {
        return scale(-1.0)
    }

    infix fun dot(other: Vector3d): Double {
        return x * other.x + y * other.y + z * other.z
    }

    infix fun cross(other: Vector3d): Vector3d {
        val x2 = other.x
        val y2 = other.y
        val z2 = other.z

        return Vector3d(
            y * z2 - z * y2,
            z * x2 - x * z2,
            x * y2 - y * x2
        )
    }

    infix fun angleBetween(other: Vector3d): Double {
        val p = this.dot(other) / (length * other.length)

        if (abs(p) > 1.0) {
            return 0.0
        }

        return acos(p) * 180 / PI
    }

    @Destructive
    fun normalize(): Vector3d {
        val l = length
        return calculate { component -> component / l }
    }

    infix fun directionTo(other: Vector3d): Vector3d {
        return deltaTo(other).normalize()
    }

    infix fun deltaTo(to: Vector3d): Vector3d {
        if (this == to) {
            throw IllegalArgumentException("2つのベクトルは完全に一致しています")
        }

        return to.copy() subtract this
    }

    infix fun distanceBetween(other: Vector3d): Double {
        return sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z))
    }

    infix fun projection(other: Vector3d): Vector3d {
        return other.copy().scale(
            other.length * length / other.length * other.length
        )
    }

    infix fun rejection(other: Vector3d): Vector3d {
        return copy().subtract(projection(other))
    }

    infix fun reflect(normal: Vector3d): Vector3d {
        return this.copy().calculate(normal) { a, b -> a - 2 * dot(normal) * b }
    }

    fun lerp(end: Vector3d, t: Float): Vector3d {
        return copy().calculate(end) { a, b -> (1 - t) * a + t * b }
    }

    fun slerp(end: Vector3d, s: Float): Vector3d {
        val angle = this.angleBetween(end) * Math.PI / 180

        val p1 = sin(angle * (1 - s)) / sin(angle)
        val p2 = sin(angle * s) / sin(angle)

        val q1 = this.copy().scale(p1)
        val q2 = end.copy().scale(p2)

        return q1.add(q2)
    }

    @Destructive
    override fun clamp(min: Vector3d, max: Vector3d): Vector3d {
        return calculate(min, max) { value, minValue, maxValue ->
            max(minValue, min(value, maxValue))
        }
    }

    infix fun middle(other: Vector3d): Vector3d {
        return copy().add(other).scale(0.5)
    }

    infix fun axisAlignedBox(other: Vector3d): AxisAlignedBoundingBox {
        return AxisAlignedBoundingBox(this, other)
    }

    operator fun rangeTo(other: Vector3d): AxisAlignedBoundingBox = axisAlignedBox(other)

    @Destructive
    fun rotate(axis: Vector3d, degree: Float): Vector3d {
        val theta = Math.toRadians(degree.toDouble())
        val sin = sin(theta)
        val cos = cos(theta)
        val k = axis.copy().normalize()
        val v = copy()

        val r = v.copy().scale(cos)
            .add(k.cross(v).scale(sin))
            .add(k.copy().scale(k.dot(v) * (1 - cos)))

        x = r.x
        y = r.y
        z = r.z
        return this
    }

    override fun format(pattern: String, digits: Int): String {
        val format = "%.${digits}f"
        return pattern
            .replace("#x".toRegex(), String.format(format, x))
            .replace("#y".toRegex(), String.format(format, y))
            .replace("#z".toRegex(), String.format(format, z))
    }

    override fun toString(): String {
        return format("(#x, #y, #z)", 2)
    }

    override fun copy(): Vector3d {
        return Vector3d(x, y, z)
    }

    fun toRotation2f(): Rotation2f {
        return Rotation2f(
            (-atan2(x / length, z / length) * 180.0 / PI).toFloat(),
            (-asin(y / length) * 180.0 / PI).toFloat()
        )
    }

    fun toPosition3i(floor: Boolean): Position3i {
        return if (floor) {
            Position3i(x.toInt(), y.toInt(), z.toInt())
        }
        else {
            Position3i(floor(x).toInt(), floor(y).toInt(), floor(z).toInt())
        }
    }

    fun toVec3(): Vec3 {
        return Vec3(x, y, z)
    }

    companion object {
        fun min(a: Vector3d, b: Vector3d): Vector3d {
            return a.copy().calculate(b) { a, b -> min(a, b) }
        }

        fun max(a: Vector3d, b: Vector3d): Vector3d {
            return a.copy().calculate(b) { a, b -> max(a, b) }
        }

        fun from(vec3: Vec3): Vector3d {
            return Vector3d(vec3.x, vec3.y, vec3.z)
        }

        val FORWARD
            get() = Vector3d(0, 0, 1)

        val BACK
            get() = Vector3d(0, 0, -1)

        val LEFT
            get() = Vector3d(1, 0, 0)

        val RIGHT
            get() = Vector3d(-1, 0, 0)

        val UP
            get() = Vector3d(0, 1, 0)

        val DOWN
            get() = Vector3d(0, -1, 0)
    }
}
