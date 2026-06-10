package io.github.takenoko4096.noctiluca.math

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

fun Vec3.toVector3d() = Vector3d.from(this)

fun Vec3i.toPosition3i() = Position3i.from(this)

fun BlockPos.toPosition3i() = Position3i.from(this)

fun Direction.toOffset() = Position3i.getOffset(this)

fun Vec2.toRotation2f() = Rotation2f.from(this)
