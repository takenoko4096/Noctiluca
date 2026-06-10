package io.github.takenoko4096.noctiluca.math

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import java.util.Objects
import java.util.stream.IntStream
import kotlin.math.max
import kotlin.math.min

class Position3i(var x: Int, var y: Int, var z: Int) : IVector<Position3i, Int> {
    override val isZero: Boolean
        get() = equals(ZERO)

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

    @Destructive
    override fun calculate(operator: (Int) -> Int): Position3i {
        x = operator(x)
        y = operator(y)
        z = operator(z)
        return this
    }

    @Destructive
    override fun calculate(other: Position3i, operator: (Int, Int) -> Int): Position3i {
        x = operator(x, other.x)
        y = operator(y, other.y)
        z = operator(z, other.z)
        return this
    }

    @Destructive
    override fun calculate(other1: Position3i, other2: Position3i, operator: (Int, Int, Int) -> Int): Position3i {
        x = operator(x, other1.x, other2.x)
        y = operator(y, other1.y, other2.y)
        z = operator(z, other1.z, other2.z)
        return this
    }

    @Destructive
    override infix fun set(other: Position3i): Position3i {
        return calculate(other) { _, b -> b }
    }

    @Destructive
    override infix fun add(other: Position3i): Position3i {
        return calculate(other) { a, b -> a + b }
    }

    @Destructive
    override infix fun subtract(other: Position3i): Position3i {
        return calculate(other) { a, b -> a - b }
    }

    @Destructive
    override infix fun scale(scalar: Int): Position3i {
        return calculate { component -> component * scalar }
    }

    @Destructive
    override infix fun divide(scalar: Int): Position3i {
        if (scalar == 0) {
            throw IllegalArgumentException("0 で割ることはできません")
        }

        return calculate { component -> component / scalar }
    }

    @Destructive
    override fun invert(): Position3i {
        return scale(-1)
    }

    @Destructive
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
        return pattern
            .replace("#x".toRegex(), x.toString())
            .replace("#y".toRegex(), y.toString())
            .replace("#z".toRegex(), z.toString())
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

        fun from(vec3i: Vec3i): Position3i {
            return Position3i(vec3i.x, vec3i.y, vec3i.z)
        }

        fun getOffset(direction: Direction): Position3i {
            return from(direction.unitVec3i)
        }

        val CODEC: Codec<Position3i> = Codec.INT_STREAM.xmap(
            {
                val array = it.toArray()
                if (array.size != 3) throw IllegalStateException("CANNOT DECODE NON-SIZE-3-ARRAY TO POSITION-3I")
                Position3i(array[0], array[1], array[2])
            },
            {
                IntStream.of(it.x, it.y, it.z)
            }
        )

        val ZERO
            get() = Position3i(0, 0, 0)

        val NORTH
            get() = Position3i(0, 0, -1)

        val SOUTH
            get() = Position3i(0, 0, 1)

        val EAST
            get() = Position3i(1, 0, 0)

        val WEST
            get() = Position3i(-1, 0, 0)

        val UP
            get() = Position3i(0, 1, 0)

        val DOWN
            get() = Position3i(0, -1, 0)
    }
}
