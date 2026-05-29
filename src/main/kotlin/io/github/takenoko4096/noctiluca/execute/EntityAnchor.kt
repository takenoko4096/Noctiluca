package io.github.takenoko4096.noctiluca.execute

import io.github.takenoko4096.noctiluca.math.Vector3d
import net.minecraft.world.entity.Entity

enum class EntityAnchor {
    FEET {
        override fun getOffset(entity: Entity): Vector3d {
            return Vector3d()
        }
    },

    EYES {
        override fun getOffset(entity: Entity): Vector3d {
            return Vector3d(0.0, entity.eyeHeight.toDouble(), 0.0)
        }
    };

    abstract fun getOffset(entity: Entity): Vector3d
}
