package io.github.takenoko4096.noctiluca.portal

import io.github.takenoko4096.noctiluca.Noctiluca
import io.github.takenoko4096.noctiluca.math.Position3i
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

data class VerticalPortal(val level: BlockGetter, val innerBottomLeftPos: Position3i, val axis: PortalAxis, val innerWidth: Int, val innerHeight: Int, val type: PortalType) {
    val frameInclusiveWidth = innerWidth + 2

    val frameInclusiveHeight = innerHeight + 2

    val frameBottomLeftPos = innerBottomLeftPos + Position3i.DOWN - axis.unit

    val frameBottomRightPos = frameBottomLeftPos + axis.unit * (frameInclusiveWidth - 1)

    val frameBlockPositions: List<Position3i>

    val portalBlockPositions: List<Position3i>

    init {
        frameBlockPositions = collectFramePositions()
        portalBlockPositions = collectPortalPositions()
    }

    private fun collectFramePositions(): List<Position3i> {
        val right = axis.unit

        val list = mutableListOf<Position3i>()

        for (u in 0..<frameInclusiveWidth) {
            val bottomPos = frameBottomLeftPos + right * u
            if (level.getBlockState(bottomPos.toBlockPos()).`is`(type.frameBlock)) {
                list.add(bottomPos)
            }

            val topPos = bottomPos + Position3i.UP * (frameInclusiveHeight - 1)
            if (level.getBlockState(topPos.toBlockPos()).`is`(type.frameBlock)) {
                list.add(topPos)
            }
        }

        for (v in 1..innerHeight) {
            val leftPos = frameBottomLeftPos + Position3i.UP * v
            if (level.getBlockState(leftPos.toBlockPos()).`is`(type.frameBlock)) {
                list.add(leftPos)
            }

            val rightPos = leftPos + right * (frameInclusiveWidth - 1)
            if (level.getBlockState(rightPos.toBlockPos()).`is`(type.frameBlock)) {
                list.add(rightPos)
            }
        }

        return list
    }

    private fun collectPortalPositions(): List<Position3i> {
        val right = axis.unit

        val list = mutableListOf<Position3i>()

        for (u in 0..<innerWidth) {
            for (v in 0..<innerHeight) {
                val currentPos = innerBottomLeftPos + (right * u) + (Position3i.UP * v)
                list.add(currentPos)
            }
        }

        return list
    }

    fun isFrameBroken(): Boolean {
        val right = axis.unit

        for (u in 1..innerWidth) {
            val bottomPos = frameBottomLeftPos + right * u
            if (!level.getBlockState(bottomPos.toBlockPos()).`is`(type.frameBlock)) {
                return true
            }

            val topPos = bottomPos + Position3i.UP * (frameInclusiveHeight - 1)
            if (!level.getBlockState(topPos.toBlockPos()).`is`(type.frameBlock)) {
                return true
            }
        }

        for (v in 1..innerHeight) {
            val leftPos = frameBottomLeftPos + Position3i.UP * v
            if (!level.getBlockState(leftPos.toBlockPos()).`is`(type.frameBlock)) {
                return true
            }

            val rightPos = leftPos + right * (frameInclusiveWidth - 1)
            if (!level.getBlockState(rightPos.toBlockPos()).`is`(type.frameBlock)) {
                return true
            }
        }

        return false
    }

    fun isFilledWith(predicate: (BlockState) -> Boolean): Boolean {
        val right = axis.unit

        for (u in 0..<innerWidth) {
            for (v in 0..<innerHeight) {
                val currentPos = innerBottomLeftPos + (right * u) + (Position3i.UP * v)
                val currentState = level.getBlockState(currentPos.toBlockPos())

                if (!predicate(currentState)) {
                    return false
                }
            }
        }

        return true
    }

    fun isCompletePortal(): Boolean {
        return isFilledWith { it.`is`(type.portalBlock) } && !isFrameBroken()
    }

    fun isIgnitable(): Boolean = isFilledWith { it.isAir }

    fun getLinkablePortal(from: Level, at: Position3i) {
        val registry = from.registryAccess().lookupOrThrow(Registries.DIMENSION)
        val dim1 = registry.getValueOrThrow(type.dimension1)
        val dim2 = registry.getValueOrThrow(type.dimension2)
        val to = if (from == dim1) dim2 else dim1

        val coordinateScaleRatio = to.dimensionType().coordinateScale / from.dimensionType().coordinateScale

        val searchBasePos = at.toVector3d() * coordinateScaleRatio

        val portalAccesses = to.globalAttachments().getAttachedOrSet(Noctiluca.PORTAL_ACCESSES, listOf())
        val nearestPortalAccess = portalAccesses.minByOrNull { it.position.toVector3d() distanceBetween searchBasePos }
        // val nearestPortal = nearestPortalAccess?.getPortal(to) ?: createPortal(level, pos, axis)
    }

    companion object {
        fun tryIgniteAt(level: Level, position: Position3i, blockState: BlockState, itemStack: ItemStack): Boolean {
            val portalType = PortalType.getByFrameBlock(blockState.block)

            val portal: VerticalPortal = portalType
                ?.portalFinder?.findPortal(level, position) { isIgnitable() } ?: return false

            if (!itemStack.`is`(portal.type.ignitionSource)) {
                return false
            }

            val axisProperty = portalType.portalBlock.getPortalAxisProperty()

            for (portalBlockPos in portal.portalBlockPositions) {
                level.setBlockAndUpdate(
                    portalBlockPos.toBlockPos(),
                    portalType.portalBlock.defaultBlockState()
                        .setValue(axisProperty, portal.axis)
                )
            }

            return true
        }
    }
}
