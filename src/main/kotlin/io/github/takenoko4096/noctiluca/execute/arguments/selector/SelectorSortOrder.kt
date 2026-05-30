package io.github.takenoko4096.noctiluca.execute.arguments.selector

import io.github.takenoko4096.noctiluca.execute.NoctilucaCommandSourceStack
import io.github.takenoko4096.noctiluca.math.Vector3d
import net.minecraft.world.entity.Entity
import kotlin.random.Random

enum class SelectorSortOrder {
    ARBITRARY {
        override fun getCriteria(stack: NoctilucaCommandSourceStack, entity: Entity): Double {
            return entity.id.toDouble()
        }
    },

    NEAREST {
        override fun getCriteria(stack: NoctilucaCommandSourceStack, entity: Entity): Double {
            return Vector3d.from(entity.position()) distanceBetween stack.getPosition()
        }
    },

    FURTHEST {
        override fun getCriteria(stack: NoctilucaCommandSourceStack, entity: Entity): Double {
            return -(Vector3d.from(entity.position()) distanceBetween stack.getPosition())
        }
    },

    RANDOM {
        override fun getCriteria(stack: NoctilucaCommandSourceStack, entity: Entity): Double {
            return Random.nextDouble()
        }
    };

    abstract fun getCriteria(stack: NoctilucaCommandSourceStack, entity: Entity): Double
}