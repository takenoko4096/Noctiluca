package io.github.takenoko4096.noctiluca.portal

import io.github.takenoko4096.noctiluca.math.Position3i
import io.github.takenoko4096.noctiluca.math.toPosition3i
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.BlockUtil.FoundRectangle
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.NetherPortalBlock
import net.minecraft.world.level.levelgen.Heightmap
import java.util.*
import kotlin.math.max
import kotlin.math.min

class PortalPlacer internal constructor(private val type: PortalType) {
    fun placePortalNearby(level: Level, center: Position3i, axis: PortalAxis) {

    }

    private fun isReplaceableByPortal(level: Level, position: Position3i): Boolean {

    }

    private fun canPlacePortalAt(level: Level, origin: Position3i, position: Position3i, direction: Direction, offset: Int): Boolean {

    }

    private fun findPlaceablePos(level: Level, center: Position3i, axis: PortalAxis): Position3i? {
        val direction = Direction.get(Direction.AxisDirection.POSITIVE, axis.toAxis())
        val worldBorder = level.worldBorder
        val maxPlaceableY: Int = min(level.maxY, level.minY + level.dimensionType().logicalHeight - 1)

        // 大量のオブジェクトを生成しないための使い回し用オブジェクト
        val mutable: Position3i = center.copy()

        var distanceToClosestSpaciousPosition = -1.0
        var closestSpaciousPosition: Position3i? = null

        var distanceToClosestNarrowPosition = -1.0
        var closestNarrowPosition: Position3i? = null

        for (currentBlockPos in BlockPos.spiralAround(mutable.toBlockPos(), 16, Direction.EAST, Direction.SOUTH)) {
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
                return null
            }

            // ポータル軸方向に1つ下がる(与えられた位置は整数座標だから生成位置が1つ下がれば転送先は2つのブロックの間になるでしょう？)
            closestSpaciousPosition = Position3i(
                center.x - direction.stepX,
                center.y.coerceIn(minStartY..maxStartY),
                center.z - direction.stepZ
            )

            // ボーダーチェックして返却
            return worldBorder.clampToBounds(closestSpaciousPosition.toBlockPos()).toPosition3i()
        }

        for (width in -1..2) {
            for (height in -1..3) {
                if (width == -1 || width == 2 || height == -1 || height == 3) {
                    mutable.setWithOffset(
                        closestSpaciousPosition,
                        width * direction.getStepX(),
                        height,
                        width * direction.getStepZ()
                    )
                    level.setBlock(mutable, Blocks.OBSIDIAN.defaultBlockState(), 3)
                }
            }
        }

        val portalBlockState = Blocks.NETHER_PORTAL.defaultBlockState()
            .setValue<Direction.Axis, Direction.Axis>(NetherPortalBlock.AXIS, axis)

        for (width in 0..1) {
            for (heightx in 0..2) {
                mutable.setWithOffset(
                    closestSpaciousPosition,
                    width * direction.getStepX(),
                    heightx,
                    width * direction.getStepZ()
                )
                level.setBlock(mutable, portalBlockState, 18)
            }
        }

        return Optional.of<FoundRectangle>(FoundRectangle(closestSpaciousPosition!!.immutable(), 2, 3))
    }

    private fun forcedPlaceAt(level: Level, position: Position3i, direction: Direction) {
        // 90度かいてーん
        val clockWise = direction.clockWise
        val currentPos: Position3i

        for (box in -1..1) {
            for (width in 0..1) {
                for (height in -1..2) {
                    val blockState =
                        if (height < 0) Blocks.OBSIDIAN.defaultBlockState() else Blocks.AIR.defaultBlockState()
                    currentPos = position + Position3i(
                        width * direction.stepX + box * clockWise.stepX,
                        height,
                        width * direction.stepZ + box * clockWise.stepZ
                    )
                    level.setBlockAndUpdate(currentPos, blockState)
                }
            }
        }
    }

    private fun place() {

    }
}
