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

    fun xSizedBox(xSize: Double, y: ClosedRange<Double>, z: ClosedRange<Double>): VoxelShape {
        val xCenter = xSize / 2.0
        return Block.box(
            8.0 - xCenter,
            y.start,
            z.start,
            8.0 + xCenter,
            y.endInclusive,
            z.endInclusive
        )
    }

    fun ySizedBox(x: ClosedRange<Double>, ySize: Double, z: ClosedRange<Double>): VoxelShape {
        val yCenter = ySize / 2.0
        return Block.box(
            x.start,
            8.0 - yCenter,
            z.start,
            x.endInclusive,
            8.0 + yCenter,
            z.endInclusive
        )
    }

    fun zSizedBox(x: ClosedRange<Double>, y: ClosedRange<Double>, zSize: Double): VoxelShape {
        val zCenter = zSize / 2.0
        return Block.box(
            x.start,
            y.start,
            8.0 - zCenter,
            x.endInclusive,
            y.endInclusive,
            8.0 + zCenter
        )
    }

    fun xySizedBox(xSize: Double, ySize: Double, z: ClosedRange<Double>): VoxelShape {
        val xCenter = xSize / 2.0
        val yCenter = ySize / 2.0
        return Block.box(
            8.0 - xCenter,
            8.0 - yCenter,
            z.start,
            8.0 + xCenter,
            8.0 + yCenter,
            z.endInclusive
        )
    }

    fun xzSizedBox(xSize: Double, y: ClosedRange<Double>, zSize: Double): VoxelShape {
        val xCenter = xSize / 2.0
        val zCenter = zSize / 2.0
        return Block.box(
            8.0 - xCenter,
            y.start,
            8.0 - zCenter,
            8.0 + xCenter,
            y.endInclusive,
            8.0 + zCenter
        )
    }

    fun yzSizedBox(x: ClosedRange<Double>, ySize: Double, zSize: Double): VoxelShape {
        val yCenter = ySize / 2.0
        val zCenter = zSize / 2.0
        return Block.box(
            x.start,
            8.0 - yCenter,
            8.0 - zCenter,
            x.endInclusive,
            8.0 + yCenter,
            8.0 + zCenter
        )
    }
}
