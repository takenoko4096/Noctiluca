package io.github.takenoko4096.noctiluca.execute

import io.github.takenoko4096.noctiluca.math.Rotation2f
import io.github.takenoko4096.noctiluca.math.Vector3d
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level

data class NoctilucaCommandSourceStack(
    val sender: NoctilucaCommandSender,
    private var executor: Entity?,
    private var position: Vector3d,
    private var rotation: Rotation2f,
    private var dimension: Level,
    private var entityAnchor: EntityAnchor
) {
    fun getExecutor(): Entity {
        return executor ?: throw IllegalStateException()
    }

    fun getExecutorOrNull(): Entity? {
        return executor
    }

    fun getPosition(): Vector3d {
        return position.copy()
    }

    fun getRotation(): Rotation2f {
        return rotation.copy()
    }

    fun getDimension(): Level {
        return dimension
    }

    fun getEntityAnchor(): EntityAnchor {
        return entityAnchor
    }

    internal fun setExecutor(entity: Entity) {
        executor = entity
    }

    internal fun setPosition(vector3d: Vector3d) {
        position = vector3d
    }
}
