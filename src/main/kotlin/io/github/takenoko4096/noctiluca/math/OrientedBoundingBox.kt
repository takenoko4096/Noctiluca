package io.github.takenoko4096.noctiluca.math

import kotlin.math.*

open class OrientedBoundingBox(
    private val _center: Vector3d,
    private val _orientation: Orientation3f,
    private val _size: Vector3d
) {
    val center: Vector3d
        get() = _center.copy()

    val orientation: Orientation3f
        get() = _orientation.copy()

    val size: Vector3d
        get() = _size.copy()

    private val halfNormals: List<Vector3d>
        get() {
            val system = orientation.localCoordinateSystem
            val x = system.x.apply { length = size.x / 2 }
            val y = system.y.apply { length = size.y / 2 }
            val z = system.z.apply { length = size.z / 2 }

            return listOf(x, y, z)
        }

    val corners: Set<Vector3d>
        get() {
            val x = halfNormals[0]
            val y = halfNormals[1]
            val z = halfNormals[2]

            return setOf(
                center.subtract(x).subtract(y).subtract(z),
                center.add(x).subtract(y).subtract(z),
                center.subtract(x).add(y).subtract(z),
                center.subtract(x).subtract(y).add(z),
                center.add(x).add(y).subtract(z),
                center.subtract(x).add(y).add(z),
                center.add(x).subtract(y).add(z),
                center.add(x).add(y).add(z)
            )
        }

    val planes: Set<OrientedBoundingPlane>
        get() {
            val system = orientation.localCoordinateSystem
            val x = halfNormals[0]
            val y = halfNormals[1]
            val z = halfNormals[2]

            val left = center.add(x)
            val right = center.subtract(x)
            val up = center.add(y)
            val down = center.subtract(y)
            val forward = center.add(z)
            val back = center.subtract(z)

            val leftPlane = OrientedBoundingPlane(left, system.left(), size.z, size.y)
            val rightPlane = OrientedBoundingPlane(right, system.right(), size.z, size.y)
            val upPlane = OrientedBoundingPlane(up, system.up(), size.x, size.z)
            val downPlane = OrientedBoundingPlane(down, system.down(), size.x, size.z)
            val forwardPlane = OrientedBoundingPlane(forward, system.forward(), size.x, size.y)
            val backPlane = OrientedBoundingPlane(back, system.back(), size.x, size.y)

            return setOf(
                leftPlane, rightPlane,
                upPlane, downPlane,
                forwardPlane, backPlane
            )
        }

    init {
        if (size.x < 0) {
            throw IllegalArgumentException("負のサイズは許容されません: x が負です: ${size.x}")
        }
        else if (size.y < 0) {
            throw IllegalArgumentException("負のサイズは許容されません: x が負です: ${size.y}")
        }
        else if (size.z < 0) {
            throw IllegalArgumentException("負のサイズは許容されません: x が負です: ${size.z}")
        }
    }

    fun isInside(point: Vector3d): Boolean {
        val x = halfNormals[0]
        val y = halfNormals[1]
        val z = halfNormals[2]

        val locations = setOf(
            center.add(z),  // forward
            center.subtract(z),  // back
            center.add(x),  // right
            center.subtract(x),  // left
            center.add(y),  // up
            center.subtract(y) // down
        )

        for (location in locations) {
            val directionToCenter = location.directionTo(center)
            val directionToPoint = location.directionTo(point)

            if (directionToCenter dot directionToPoint < 0) {
                return false
            }
        }

        return true
    }

    operator fun contains(point: Vector3d): Boolean = isInside(point)

    fun isCollides(other: OrientedBoundingBox): Boolean {
        // Separating Axis Theorem (SAT: 分離軸定理)
        val systemA = orientation.localCoordinateSystem
        val systemB = other.orientation.localCoordinateSystem

        // Aのそれぞれの面の法線ベクトル
        val ax = systemA.x
        val ay = systemA.y
        val az = systemA.z
        // Bのそれぞれの面の法線ベクトル
        val bx = systemB.x
        val by = systemB.y
        val bz = systemB.z

        // A, Bの法線ベクトル同士の外積を含む15の分離軸
        val axes = setOf(
            ax, ay, az,
            bx, by, bz,
            ax.cross(bx), ax.cross(by), ax.cross(bz),
            ay.cross(bx), ay.cross(by), ay.cross(bz),
            az.cross(bx), az.cross(by), az.cross(bz)
        )

        // Aの頂点座標
        val cornersA = corners
        // Bの頂点座標
        val cornersB = other.corners

        // それぞれの分離軸について
        for (axis in axes) {
            // 分離軸とAの頂点座標の内積たち
            val vA = cornersA.map { axis dot it }.toSet()
            // 分離軸とBの頂点座標の内積たち
            val vB = cornersB.map { axis dot it }.toSet()

            // vAの最小値と最大値
            val minA = vA.reduce(::min)
            val maxA = vA.reduce(::max)

            // vBの最小値と最大値
            val minB = vB.reduce(::min)
            val maxB = vB.reduce(::max)

            // 範囲が重なっている = その分離軸においてA, Bは衝突している
            if (!(minA > maxB || minB > maxA)) {
                // 次の軸も確かめる
                continue
            }
            else {
                // 衝突していない！ -> false
                return false
            }
        }

        // 全軸が衝突を返したとき
        return true
    }

    fun rayCast(from: Vector3d, to: Vector3d): Vector3d? {
        val intersections = mutableListOf<Vector3d>()

        for (plane in planes) {
            val intersection = plane.rayCast(from, to) ?: continue
            intersections.add(intersection)
        }

        if (intersections.isEmpty()) {
            return null
        }

        return intersections
            .map { intersection -> intersection to from.distanceBetween(intersection) }
            .sortedWith { a, b -> (a.second - b.second).toInt() }
            .first().first
    }
}
