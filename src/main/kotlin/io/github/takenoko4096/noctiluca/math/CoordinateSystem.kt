package io.github.takenoko4096.noctiluca.math

import kotlin.math.*

class CoordinateSystem private constructor(orientation: Orientation3f) {
    private val orientation: Orientation3f = orientation.copy()

    val x: Vector3d
        get() {
            val forward = z
            return Vector3d(forward.z, 0.0, -forward.x).normalize()
                .rotate(forward, orientation.roll)
        }

    val y: Vector3d
        get() = z.cross(x)

    val z: Vector3d
        get() = orientation.toDirection3d()

    fun forward(): Orientation3f {
        return orientation.copy()
    }

    fun back(): Orientation3f {
        return orientation(x.invert(), y)
    }

    fun left(): Orientation3f {
        return orientation(z.invert(), y)
    }

    fun right(): Orientation3f {
        return orientation(z, y)
    }

    fun up(): Orientation3f {
        return orientation(x, z.invert())
    }

    fun down(): Orientation3f {
        return orientation(x, z)
    }

    private fun orientation(x: Vector3d, y: Vector3d): Orientation3f {
        val z = x.cross(y)

        return Orientation3f(
            (atan2(-z.x, z.z) * 180.0 / PI).toFloat(),
            (asin(-z.y) * 180 / PI).toFloat(),
            (atan2(x.y, y.y) * 180.0 / PI).toFloat()
        )
    }

    companion object {
        internal fun local(orientation3F: Orientation3f): CoordinateSystem {
            return CoordinateSystem(orientation3F)
        }
    }
}
