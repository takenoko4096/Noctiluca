package io.github.takenoko4096.noctiluca.execute.arguments.selector

import io.github.takenoko4096.noctiluca.execute.NoctilucaCommandSourceStack
import io.github.takenoko4096.noctiluca.math.Vector3d
import net.minecraft.world.entity.Entity

enum class SelectorSortOrder {
    ARBITRARY {
        override fun sort(stack: NoctilucaCommandSourceStack, entities: List<Entity>): List<Entity> {
            return entities.sortedBy { it.id }
        }
    },

    DISTANCE {
        override fun sort(stack: NoctilucaCommandSourceStack, entities: List<Entity>): List<Entity> {
            return entities.sortedBy { Vector3d.Companion.from(it.position()) distanceBetween stack.getPosition() }
        }
    },

    RANDOM {
        override fun sort(stack: NoctilucaCommandSourceStack, entities: List<Entity>): List<Entity> {
            return entities.shuffled()
        }
    };

    abstract fun sort(stack: NoctilucaCommandSourceStack, entities: List<Entity>): List<Entity>
}