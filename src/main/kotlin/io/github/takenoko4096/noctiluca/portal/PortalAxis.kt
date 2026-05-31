package io.github.takenoko4096.noctiluca.portal

import io.github.takenoko4096.noctiluca.math.Position3i

enum class PortalAxis(val unit: Position3i) {
    X(Position3i(1, 0, 0)),
    Z(Position3i(0, 0, 1))
}
