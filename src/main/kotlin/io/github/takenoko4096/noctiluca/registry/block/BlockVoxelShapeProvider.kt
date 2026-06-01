package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.math.Position3i
import io.github.takenoko4096.noctiluca.math.Vector3d
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

@NoctilucaDsl
class BlockVoxelShapeProvider internal constructor(
    val blockState: BlockState,
    val level: BlockGetter,
    val position: Position3i,
    val context: CollisionContext
) {
    fun box(from: Vector3d, to: Vector3d): VoxelShape {
        val min = Vector3d.min(from, to)
        val max = Vector3d.max(from, to)
        return Block.box(min.x, min.y, min.z, max.x, max.y, max.z)
    }
}
