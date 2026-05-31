package io.github.takenoko4096.noctiluca.portal

import io.github.takenoko4096.noctiluca.math.Position3i
import net.minecraft.core.Direction
import net.minecraft.util.StringRepresentable

enum class PortalAxis(val unit: Position3i) : StringRepresentable {
    X(Position3i(1, 0, 0)) {
        override fun toAxis(): Direction.Axis {
            return Direction.Axis.X
        }

        override fun toString(): String {
            return "x"
        }
    },
    Z(Position3i(0, 0, 1)) {
        override fun toAxis(): Direction.Axis {
            return Direction.Axis.Z
        }

        override fun toString(): String {
            return "z"
        }
    };

    abstract fun toAxis(): Direction.Axis

    override fun getSerializedName(): String {
        return toString()
    }

    abstract override fun toString(): String
}
