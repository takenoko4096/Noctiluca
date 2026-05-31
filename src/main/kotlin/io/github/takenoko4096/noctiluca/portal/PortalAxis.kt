package io.github.takenoko4096.noctiluca.portal

import io.github.takenoko4096.noctiluca.math.Position3i
import net.minecraft.core.Direction

enum class PortalAxis(val unit: Position3i) {
    X(Position3i(1, 0, 0)) {
        override fun toAxis(): Direction.Axis {
            return Direction.Axis.X
        }
    },
    Z(Position3i(0, 0, 1)) {
        override fun toAxis(): Direction.Axis {
            return Direction.Axis.Z
        }
    };

    abstract fun toAxis(): Direction.Axis
}
