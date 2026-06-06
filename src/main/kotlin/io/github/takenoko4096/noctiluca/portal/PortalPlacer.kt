package io.github.takenoko4096.noctiluca.portal

import io.github.takenoko4096.noctiluca.math.Position3i
import io.github.takenoko4096.noctiluca.math.toPosition3i
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.Heightmap
import kotlin.math.max
import kotlin.math.min

class PortalPlacer internal constructor(private val type: PortalType) {
    fun placePortalNearby(level: Level, center: Position3i, axis: PortalAxis): CustomPortal? {
        return when (val destination = findPlaceablePos(level, center, axis)) {
            is PositionSearchResult.Uncreatable -> null
            is PositionSearchResult.ForcedCreation -> {
                createSpace(level, destination.position, axis)
                createPortal(level, destination.position, axis)
            }
            is PositionSearchResult.Creatable -> {
                createPortal(level, destination.position, axis)
            }
        }
    }

    private fun isReplaceableByPortal(level: Level, position: Position3i): Boolean {
        val blockState: BlockState = level.getBlockState(position.toBlockPos())
        return blockState.canBeReplaced() && blockState.fluidState.isEmpty
    }

    private fun canPlacePortalAt(level: Level, mutable: Position3i, position: Position3i, direction: Direction, offset: Int): Boolean {
        val clockWise = direction.clockWise

        for (width in -1..2) {
            for (height in -1..3) {
                mutable.set(position)
                mutable.x += direction.stepX * width + clockWise.stepX * offset
                mutable.y += height
                mutable.z += direction.stepZ * width + clockWise.stepZ * offset

                if (height < 0 && !level.getBlockState(mutable.toBlockPos()).isSolid) {
                    return false
                }

                if (height >= 0 && !isReplaceableByPortal(level, mutable)) {
                    return false
                }
            }
        }

        return true
    }

    private fun findPlaceablePos(level: Level, center: Position3i, axis: PortalAxis): PositionSearchResult {
        val direction = Direction.get(Direction.AxisDirection.POSITIVE, axis.toAxis())
        val worldBorder = level.worldBorder
        val maxPlaceableY: Int = min(level.maxY, level.minY + level.dimensionType().logicalHeight - 1)

        // 大量のオブジェクトを生成しないための使い回し用オブジェクト
        val mutable: Position3i = center.copy()

        var distanceToClosestSpaciousPosition = -1.0
        var closestSpaciousPosition: Position3i? = null

        var distanceToClosestNarrowPosition = -1.0
        var closestNarrowPosition: Position3i? = null

        for (currentBlockPos in BlockPos.spiralAround(center.toBlockPos(), 16, Direction.EAST, Direction.SOUTH)) {
            val currentPos = currentBlockPos.toPosition3i()

            // 地表の高さを求める
            val surfaceHeight = min(
                maxPlaceableY,
                level.getHeight(Heightmap.Types.MOTION_BLOCKING, currentPos.x, currentPos.z)
            )

            // ポータル内部の2マス分がどちらもボーダー内か？
            if (worldBorder.isWithinBounds(currentPos.toBlockPos()) && worldBorder.isWithinBounds((currentPos + axis.unit).toBlockPos())) {
                var y = surfaceHeight

                // 地表の高さとさらにその下の空洞すべてについて
                while (y >= level.minY) {
                    currentPos.y = y

                    // 空洞を見つけたらば
                    if (isReplaceableByPortal(level, currentPos)) {
                        // その高さを保存
                        val firstEmptyY = y

                        // 空洞の一番下まで下がる
                        while (y > level.minY && isReplaceableByPortal(level, currentPos + Position3i.DOWN)) y--

                        // ポータルの天井(フレームの一番上)の高さ
                        val portalCeilingY = y + 4

                        // ポータルの天井の高さが最大高度超えてたらポータルは置けないので次の空洞を探しに行く
                        if (portalCeilingY > maxPlaceableY) {
                            y--
                            continue
                        }

                        // 空洞の一番上から空洞の一番下までの幅
                        val deltaY = firstEmptyY - y + 1

                        // 高さ2～3マスの空洞にさよならを告げて
                        // deltaY=1は地表の高さでの探索のためにこの数字が出てしまっている可能性があるので弾かない
                        // 幅4マスあればひとつ下の高さを床にしてポータルは置けるから2～3で弾いています
                        if (deltaY in 2..3) {
                            y--
                            continue
                        }

                        currentPos.y = y

                        // ズバリここにポータル、置けますか？
                        if (canPlacePortalAt(level, mutable, currentPos, direction, 0)) {
                            // 探索開始座標からの距離
                            val distanceFromSearchingCenter = center.toVector3d() distanceBetween currentPos.toVector3d()

                            // ポータルの前後に幅があり、かつここが初めて見つける広い場所or既に見つかっている広い場所よりさらに探索基準位置に近い場所だったら保存
                            if (canPlacePortalAt(level, mutable,  currentPos, direction, -1)
                                && canPlacePortalAt(level, mutable,  currentPos, direction, 1)
                                && (distanceToClosestSpaciousPosition == -1.0 || distanceToClosestSpaciousPosition > distanceFromSearchingCenter)
                            ) {
                                distanceToClosestSpaciousPosition = distanceFromSearchingCenter
                                closestSpaciousPosition = currentPos.copy()
                            }

                            // 広い場所がまだ見つかっていない、かつここが初めて見つける狭い場所or既に見つかっている狭い場所よりさらに探索基準位置に近い場所だったら保存
                            if (distanceToClosestSpaciousPosition == -1.0 && (distanceToClosestNarrowPosition == -1.0 || distanceToClosestNarrowPosition > distanceFromSearchingCenter)) {
                                distanceToClosestNarrowPosition = distanceFromSearchingCenter
                                closestNarrowPosition = currentPos.copy()
                            }
                        }
                    }

                    y--
                }
            }
        }

        // 狭い場所しか見つからなかったならそれを渋々採用
        if (distanceToClosestSpaciousPosition == -1.0 && distanceToClosestNarrowPosition != -1.0) {
            closestSpaciousPosition = closestNarrowPosition
            distanceToClosestSpaciousPosition = distanceToClosestNarrowPosition
        }

        // 狭い場所すらも見つからなかった！
        if (distanceToClosestSpaciousPosition == -1.0) {
            // 生成開始位置の高さの最小・最大
            val minStartY = max(level.minY + 1, 70)
            val maxStartY = maxPlaceableY - 9

            if (maxStartY < minStartY) {
                // マジでポータル生成不可能
                return PositionSearchResult.Uncreatable
            }

            // ポータル軸方向に1つ下がる(与えられた位置は整数座標だから生成位置が1つ下がれば転送先は2つのブロックの間になるでしょう？)
            closestSpaciousPosition = Position3i(
                center.x - direction.stepX,
                center.y.coerceIn(minStartY..maxStartY),
                center.z - direction.stepZ
            )

            // ボーダーチェックして返却
            return PositionSearchResult.ForcedCreation(worldBorder.clampToBounds(closestSpaciousPosition.toBlockPos()).toPosition3i())
        }

        // Maybe Never Happens
        if (closestSpaciousPosition == null) return PositionSearchResult.Uncreatable

        return PositionSearchResult.Creatable(closestSpaciousPosition)
    }

    private fun createSpace(level: Level, position: Position3i, axis: PortalAxis) {
        val direction = Direction.get(Direction.AxisDirection.POSITIVE, axis.toAxis())

        // 90度かいてーん
        val clockWise = direction.clockWise

        // 負荷軽減用可変整数座標
        val currentPos: Position3i = Position3i.ZERO

        for (box in -1..1) {
            for (width in 0..1) {
                for (height in -1..2) {
                    val blockState = Blocks.AIR.defaultBlockState()
                    currentPos.set(position)
                    currentPos.x += width * direction.stepX + box * clockWise.stepX
                    currentPos.y += height
                    currentPos.z += width * direction.stepZ + box * clockWise.stepZ
                    level.setBlockAndUpdate(currentPos.toBlockPos(), blockState)
                }
            }
        }
    }

    private fun createPortal(level: Level, position: Position3i, axis: PortalAxis): CustomPortal? {
        val direction = Direction.get(Direction.AxisDirection.POSITIVE, axis.toAxis())

        // 負荷軽減用可変整数座標
        val currentPos: Position3i = Position3i.ZERO

        val frameBlockState = type.frameBlock.defaultBlockState()
        val portalBlockState = type.portalBlock.defaultBlockState().setValue(type.portalBlock.getPortalAxisProperty(), axis)

        for (width in -1..2) {
            for (height in -1..3) {
                if (width == -1 || width == 2 || height == -1 || height == 3) {
                    currentPos.set(position)
                    currentPos.x += width * direction.stepX
                    currentPos.y += height
                    currentPos.z += width * direction.stepZ
                    level.setBlock(currentPos.toBlockPos(), frameBlockState, 3)
                }
            }
        }

        for (width in 0..1) {
            for (height in 0..2) {
                currentPos.set(position)
                currentPos.x += width * direction.stepX
                currentPos.y += height
                currentPos.z += width * direction.stepZ
                level.setBlock(currentPos.toBlockPos(), portalBlockState, 18)
            }
        }

        return CustomPortal(
            level,
            position,
            axis,
            2,
            3,
            type
        )
    }

    sealed class PositionSearchResult {
        object Uncreatable : PositionSearchResult()
        class ForcedCreation(val position: Position3i) : PositionSearchResult()
        class Creatable(val position: Position3i) : PositionSearchResult()
    }
}
