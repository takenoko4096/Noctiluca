package io.github.takenoko4096.noctiluca.portal

import io.github.takenoko4096.noctiluca.math.Position3i
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

data class VerticalPortal(val level: Level, val innerBottomLeftPos: Position3i, val axis: PortalAxis, val innerWidth: Int, val innerHeight: Int, val frame: Block, val portal: Block) {
    val frameInclusiveWidth = innerWidth + 2

    val frameInclusiveHeight = innerHeight + 2

    val frameBottomLeftPos = innerBottomLeftPos + Position3i.DOWN - axis.unit

    val frameBottomRightPos = frameBottomLeftPos + axis.unit * (frameInclusiveWidth - 1)

    val framePositions: List<Position3i>

    val portalPositions: List<Position3i>

    init {
        framePositions = collectFramePositions()
        portalPositions = collectPortalPositions()
        portals.add(this)
    }

    val isLoadable: Boolean
        get() {
            return level.isLoaded(frameBottomLeftPos.toBlockPos()) && level.isLoaded(frameBottomRightPos.toBlockPos())
        }

    private fun collectFramePositions(): List<Position3i> {
        val right = axis.unit

        val list = mutableListOf<Position3i>()

        for (u in 0..<frameInclusiveWidth) {
            val bottomPos = frameBottomLeftPos + right * u
            if (level.getBlockState(bottomPos.toBlockPos()).`is`(frame)) {
                list.add(bottomPos)
            }

            val topPos = bottomPos + Position3i.UP * (frameInclusiveHeight - 1)
            if (level.getBlockState(topPos.toBlockPos()).`is`(frame)) {
                list.add(topPos)
            }
        }

        for (v in 1..innerHeight) {
            val leftPos = frameBottomLeftPos + Position3i.UP * v
            if (level.getBlockState(leftPos.toBlockPos()).`is`(frame)) {
                list.add(leftPos)
            }

            val rightPos = leftPos + right * (frameInclusiveWidth - 1)
            if (level.getBlockState(rightPos.toBlockPos()).`is`(frame)) {
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
            if (!level.getBlockState(bottomPos.toBlockPos()).`is`(frame)) {
                return true
            }

            val topPos = bottomPos + Position3i.UP * (frameInclusiveHeight - 1)
            if (!level.getBlockState(topPos.toBlockPos()).`is`(frame)) {
                return true
            }
        }

        for (v in 1..innerHeight) {
            val leftPos = frameBottomLeftPos + Position3i.UP * v
            if (!level.getBlockState(leftPos.toBlockPos()).`is`(frame)) {
                return true
            }

            val rightPos = leftPos + right * (frameInclusiveWidth - 1)
            if (!level.getBlockState(rightPos.toBlockPos()).`is`(frame)) {
                return true
            }
        }

        return false
    }

    fun isFilledWithPortalBlock(): Boolean {
        val right = axis.unit

        for (u in 0..<innerWidth) {
            for (v in 0..<innerHeight) {
                val currentPos = innerBottomLeftPos + (right * u) + (Position3i.UP * v)

                if (!level.getBlockState(currentPos.toBlockPos()).`is`(portal)) {
                    return false
                }
            }
        }

        return true
    }

    private fun tick() {
        if (isFrameBroken() || !isFilledWithPortalBlock()) {
            for (pos in portalPositions) {
                level.destroyBlock(pos.toBlockPos(), false)
            }

            portals.remove(this)
        }
    }

    companion object {
        private val portals = mutableSetOf<VerticalPortal>()

        init {
            ServerTickEvents.END_SERVER_TICK.register {
                for (portal in portals.toSet()) {
                    if (portal.isLoadable) portal.tick()
                }
            }
        }
    }
}
