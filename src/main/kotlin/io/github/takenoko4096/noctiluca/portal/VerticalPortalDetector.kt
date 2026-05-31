package io.github.takenoko4096.noctiluca.portal

import io.github.takenoko4096.noctiluca.math.Position3i
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.max

class VerticalPortalDetector(val frame: Block, val portal: Block) {
    fun findPortal(level: Level, position: Position3i): VerticalPortal? {
        return findPortalWithAxis(level, position, PortalAxis.X) ?: findPortalWithAxis(level, position, PortalAxis.Z)
    }

    private fun findPortalWithAxis(level: Level, position: Position3i, axis: PortalAxis): VerticalPortal? {
        val innerBottomLeftPos = findInnerBottomLeft(level, position, axis) ?: return null
        val innerWidth = measureInnerWidthWithFloorValidation(level, innerBottomLeftPos, axis) ?: return null
        val innerHeight = measureInnerHeightWithWallsAndCeilValidation(level, innerBottomLeftPos, innerWidth, axis) ?: return null
        return VerticalPortal(level, innerBottomLeftPos, axis, innerWidth, innerHeight, frame, portal)
    }

    private fun isObstacle(blockState: BlockState): Boolean {
        return !blockState.`is`(frame) && !blockState.isAir && !blockState.`is`(portal)
    }

    private fun findInnerBottomLeft(level: Level, position: Position3i, axis: PortalAxis): Position3i? {
        // ポータルフレームのありうる最低の高さをディメンションの最低高度でクランプ
        val minY = max(level.minY, position.y - MAX_HEIGHT)

        var currentPos = position

        // 一番下まで行く
        while (currentPos.y > minY) {
            val belowPos = currentPos + Position3i.DOWN
            val belowState = level.getBlockState(belowPos.toBlockPos())

            if (belowState.`is`(frame)) break
            else if (isObstacle(belowState)) return null
            else currentPos = belowPos
        }

        // これは左向きベクトル
        val left = -axis.unit

        // 下を這うように左向きに進む
        var i = 0
        while (i <= MAX_WIDTH) {
            val leftPos = currentPos + left
            val leftState = level.getBlockState(leftPos.toBlockPos())
            if (leftState.`is`(frame)) {
                // 現在の位置のひとつ左の位置がフレームならここを左下として返す
                return currentPos
            }
            else if (isObstacle(leftState)) {
                // 現在の位置のひとつ左の位置が邪魔なブロックなら探索失敗
                return null
            }

            // ひとつ左が空気なら続行
            val leftBelowPos = leftPos + Position3i.DOWN
            val leftBelowState = level.getBlockState(leftBelowPos.toBlockPos())
            if (leftBelowState.`is`(frame)) {
                // 左の位置のひとつ下がフレームならさらに左へ
                currentPos = leftPos
            }
            else {
                // 左の位置のひとつ下がフレームでないなら探索失敗
                return null
            }

            i++
        }

        return null
    }

    private fun measureInnerWidthWithFloorValidation(level: Level, innerBottomLeftPos: Position3i, axis: PortalAxis): Int? {
        // これは右向きベクトル
        val right = axis.unit

        var currentPos = innerBottomLeftPos

        // 0から開始すればフレームブロックが初回で現れたときにinnerWidth=0を返すことができる
        for (width in 0..MAX_WIDTH) {
            val blockState = level.getBlockState(currentPos.toBlockPos())
            if (blockState.`is`(frame)) {
                if (width < MIN_WIDTH) return null
                return width
            }
            if (isObstacle(blockState)) return null

            val belowState = level.getBlockState((currentPos + Position3i.DOWN).toBlockPos())
            if (!belowState.`is`(frame)) return null

            currentPos += right
        }

        return null
    }

    private fun measureInnerHeightWithWallsAndCeilValidation(level: Level, innerBottomLeftPos: Position3i, innerWidth: Int, axis: PortalAxis): Int? {
        val right = axis.unit
        val left = -right
        val leftToRightVec = right * (innerWidth - 1)

        var innerLeftPos = innerBottomLeftPos

        // 0から開始すればフレームブロックが初回で現れたときにinnerHeight=0を返すことができる
        for (height in 0..MAX_HEIGHT) {
            val innerRightPos = innerLeftPos + leftToRightVec

            // 上にぶつかったらそれが障害物でないか確認
            val innerLeftState = level.getBlockState(innerLeftPos.toBlockPos())
            if (innerLeftState.`is`(frame)) {
                // フレームブロックだった場合、さらに端から端まですべてがフレームブロックか確認
                for (i in 0..<innerWidth) {
                    val currentPos = innerLeftPos + (right * i)
                    val currentState = level.getBlockState(currentPos.toBlockPos())
                    if (!currentState.`is`(frame)) return null
                }

                if (height < MIN_HEIGHT) return null
                return height
            }
            else if (isObstacle(innerLeftState)) {
                // 障害物だったケース
                return null
            }

            // 左右フレームがあるかのチェック
            val leftEdgeState = level.getBlockState((innerLeftPos + left).toBlockPos())
            val rightEdgeState = level.getBlockState((innerRightPos + right).toBlockPos())
            if (!leftEdgeState.`is`(frame) || !rightEdgeState.`is`(frame)) return null

            // 左右フレームの間に邪魔なものがないか確認
            for (i in 0..<innerWidth) {
                val currentPos = innerLeftPos + (right * i)
                val currentState = level.getBlockState(currentPos.toBlockPos())
                if (currentState.`is`(frame) || isObstacle(currentState)) return null
            }

            innerLeftPos += Position3i.UP
        }

        return null
    }

    companion object {
        private const val MIN_WIDTH = 2

        private const val MAX_WIDTH = 21

        private const val MIN_HEIGHT = 3

        private const val MAX_HEIGHT = 21
    }
}
