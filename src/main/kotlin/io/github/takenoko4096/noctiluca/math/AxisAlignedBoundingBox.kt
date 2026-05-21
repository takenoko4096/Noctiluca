package io.github.takenoko4096.noctiluca.math

class AxisAlignedBoundingBox(private val a: Vector3d, private val b: Vector3d) : OrientedBoundingBox(a middle b, Orientation3f(), Vector3d.max(a, b) subtract Vector3d.min(a, b)) {
    val min
        get() = Vector3d.min(a, b)

    val max
        get() = Vector3d.max(a, b)
}
