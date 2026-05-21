package io.github.takenoko4096.noctiluca.math

import net.minecraft.core.BlockPos
import java.util.Objects
import kotlin.math.max
import kotlin.math.min

class Position3i(var x: Int, var y: Int, var z: Int) : IVector<Position3i, Int> {
    override val isZero: Boolean
        get() = equals(Position3i(x, y, z))

    override val components: List<Int>
        get() {
            return listOf(x, y, z)
        }

    override fun hashCode(): Int {
        return Objects.hash(x, y, z)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (other !is Position3i) return false
        return x == other.x && y == other.y && z == other.z
    }

    override fun calculate(operator: (Int) -> Int): Position3i {
        x = operator(x)
        y = operator(y)
        z = operator(z)
        return this
    }

    override fun calculate(other: Position3i, operator: (Int, Int) -> Int): Position3i {
        x = operator(x, other.x)
        y = operator(y, other.y)
        z = operator(z, other.z)
        return this
    }

    override fun calculate(other1: Position3i, other2: Position3i, operator: (Int, Int, Int) -> Int): Position3i {
        x = operator(x, other1.x, other2.x)
        y = operator(y, other1.y, other2.y)
        z = operator(z, other1.z, other2.z)
        return this
    }

    override infix fun add(other: Position3i): Position3i {
        return calculate(other) { a, b -> a + b }
    }

    override infix fun subtract(other: Position3i): Position3i {
        return calculate(other) { a, b -> a - b }
    }

    override infix fun scale(scalar: Int): Position3i {
        return calculate { component -> component * scalar }
    }

    override infix fun divide(scalar: Int): Position3i {
        if (scalar == 0) {
            throw IllegalArgumentException("0 で割ることはできません")
        }

        return calculate { component -> component / scalar }
    }

    override fun invert(): Position3i {
        return scale(-1)
    }

    override fun clamp(min: Position3i, max: Position3i): Position3i {
        return calculate(min, max) { value, minValue, maxValue ->
            max(minValue, min(value, maxValue))
        }
    }

    fun axisAlignedBox(other: Position3i): AxisAlignedBoundingBox {
        val min = Vector3d.min(toVector3d(), other.toVector3d())
        val max = Vector3d.max(toVector3d(), other.toVector3d())

        return AxisAlignedBoundingBox(
            min,
            max.add(Vector3d(1, 1, 1))
        )
    }

    override fun format(pattern: String, digits: Int): String {
        val format = "%.${digits}f"
        return pattern
            .replace("#x".toRegex(), String.format(format, x))
            .replace("#y".toRegex(), String.format(format, y))
            .replace("#z".toRegex(), String.format(format, z))
    }

    override fun copy(): Position3i {
        return Position3i(x, y, z)
    }

    override fun toString(): String {
        return format("(#x, #y, #z)", 2)
    }

    fun toVector3d(): Vector3d {
        return Vector3d(x, y, z)
    }

    fun bottomCenter(): Vector3d {
        return toVector3d() + Vector3d(0.5, 0.0, 0.5)
    }

    fun toBlockPos(): BlockPos {
        return BlockPos(x, y, z)
    }

    companion object {
        fun from(blockPos: BlockPos): Position3i {
            return Position3i(blockPos.x, blockPos.y, blockPos.z)
        }
    }
}
