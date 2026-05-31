package io.github.takenoko4096.noctiluca.math

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

fun Vec3.toVector3d() = Vector3d.from(this)

fun BlockPos.toPosition3i() = Position3i.from(this)
