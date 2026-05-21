package io.github.takenoko4096.noctiluca.math

import java.util.Objects
import kotlin.math.*

class OrientedBoundingPlane internal constructor(
    private val _center: Vector3d,
    private val _orientation: Orientation3f,
    val width: Double,
    val height: Double
) {
    val center: Vector3d
        get() = _center.copy()

    val orientation: Orientation3f
        get() = _orientation.copy()

    val normal: Vector3d
        get() = orientation.toDirection3d()

    private val sortedCorners: List<Vector3d>
        get() {
            val system = orientation.localCoordinateSystem

            val vx = system.x.apply { length = width / 2 }
            val vy = system.y.apply { length = height / 2 }

            return listOf(
                center.subtract(vx).subtract(vy),
                center.add(vx).subtract(vy),
                center.subtract(vx).add(vy),
                center.add(vx).add(vy)
            )
        }

    override fun hashCode(): Int {
        return Objects.hash(center, orientation, width, height)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is OrientedBoundingPlane) return false
        return center == other.center && orientation == other.orientation && width == other.width && height == other.height
    }

    fun rayCast(from: Vector3d, to: Vector3d): Vector3d? {
        // "始点->平面の中心"の法線方向射影 / "始点->終点"の法線方向射影 をすれば割合は求まるでしょう？
        // 射影するのは向きを合わせるためだよ
        // 内積の理解なんてふんわり射影でいいんよ、a dot b = aのb方向成分 * |b|, |b|は割合を取る用途なら割り算で相殺される
        val ratio = ((from deltaTo center) dot normal) / ((from deltaTo to) dot normal)

        // 線分の範囲外ですね
        if (ratio !in 0.0..1.0) return null

        // from->to の ratio 倍ベクトルは from から見ると平面との交点を指すよね
        val delta = from deltaTo to
        val intersection = from.copy() add (delta scale ratio)

        // --- 四角形の内側判定 ---
        for (i in sortedCorners.indices) {
            val a = sortedCorners[i]
            val b = sortedCorners[(i + 1) % sortedCorners.size]

            // aから伸びる縁ベクトル
            val edgeVec = a deltaTo b

            // aから伸びる交点を指すベクトル
            val pointerVec = a deltaTo intersection

            // 法線と逆向き -> 四角形の外側
            if (((edgeVec cross pointerVec) dot normal) < 0) {
                return null
            }
        }

        return intersection
    }

    fun distanceBetween(point: Vector3d): Double {
        return abs((center deltaTo point) dot normal)
    }
}
