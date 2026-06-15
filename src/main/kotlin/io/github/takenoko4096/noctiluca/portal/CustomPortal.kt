package io.github.takenoko4096.noctiluca.portal

import io.github.takenoko4096.noctiluca.Noctiluca
import io.github.takenoko4096.noctiluca.math.Position3i
import io.github.takenoko4096.noctiluca.math.Vector3d
import io.github.takenoko4096.noctiluca.math.toOffset
import io.github.takenoko4096.noctiluca.math.toPosition3i
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.portal.TeleportTransition
import net.minecraft.world.phys.BlockHitResult

class CustomPortal(val level: BlockGetter, val innerBottomLeftPos: Position3i, val axis: PortalAxis, val innerWidth: Int, val innerHeight: Int, val type: PortalType) {
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

    fun isIgnitable(): Boolean = isFilledWith {
        it.isAir || type.ignitionSources.any { ignitionSource -> ignitionSource is PortalIgnitionSource.SourceBlock && it.`is`(ignitionSource.source) }
    }

    fun getOrCreateLinkablePortal(from: ServerLevel, at: Position3i): CustomPortal? {
        val server = from.server!!
        val dim1 = server.getLevel(type.dimension1)
        val dim2 = server.getLevel(type.dimension2)

        if (dim1 == null) {
            return null
        }

        if (dim2 == null) {
            return null
        }

        val to = if (from == dim1) dim2 else dim1

        val coordinateScaleRatio = from.dimensionType().coordinateScale / to.dimensionType().coordinateScale

        val searchBasePos = at.toVector3d() * coordinateScaleRatio

        val portalAccesses = to.globalAttachments()
            .getAttachedOrSet(Noctiluca.PORTAL_ACCESSES, mapOf())[to.dimension().identifier()]
            ?.filter { PortalType.get(it.type) == type }
            ?: listOf()

        Noctiluca.logger.info("portal access list on '${to.dimension().identifier()}': {}", portalAccesses)

        val nearestPortalAccess = portalAccesses
            .associateWith { it.position.toVector3d().apply { this.y = 0.0 } distanceBetween searchBasePos.copy().apply { this.y = 0.0 } }
            .entries.minByOrNull { it.value }
            ?.takeIf {
                Noctiluca.logger.info("nearest access: ${it.key} with distance ${it.value}, compared with ${(PORTAL_SEARCH_DISTANCE * coordinateScaleRatio)}")
                it.value < (PORTAL_SEARCH_DISTANCE * coordinateScaleRatio)
            }
            ?.key

        return nearestPortalAccess?.getPortal(to)
            ?: type.portalPlacer.placePortalNearby(to, searchBasePos.toPosition3i(false), axis)
    }

    internal fun getTeleportTransition(from: ServerLevel): TeleportTransition? {
        val server = from.server!!
        val dim1 = server.getLevel(type.dimension1)
        val dim2 = server.getLevel(type.dimension2)

        if (dim1 == null) {
            Noctiluca.logger.info("Could not find dimension ${type.dimension1} in getTeleportTransition()")
            return null
        }

        if (dim2 == null) {
            Noctiluca.logger.info("Could not find dimension ${type.dimension2} in getTeleportTransition()")
            return null
        }

        val to = if (from == dim1) dim2 else dim1
        val rot = axis.opposite().unit.toVector3d().toRotation2f()
        val pos = innerBottomLeftPos.toVector3d().add(axis.unit.toVector3d() * 0.5)

        return TeleportTransition(
            to,
            pos.toVec3(),
            Vector3d().toVec3(),
            rot.yaw,
            rot.pitch,
            TeleportTransition.PLAY_PORTAL_SOUND.then { entity ->
                entity.placePortalTicket(pos.toPosition3i(false).toBlockPos())
            }
        )
    }

    fun toAccess(): PortalAccess = PortalAccess(
        type.identifier,
        innerBottomLeftPos,
        axis
    )

    fun ignite(level: Level, source: PortalIgnitionSource<*>): Boolean {
        if (isCompletePortal()) return false
        if (source !in type.ignitionSources) return false

        val currentLevelResourceKey = level.dimension()
        if (!(currentLevelResourceKey == type.dimension1 || currentLevelResourceKey == type.dimension2)) {
            return false
        }

        val axisProperty = type.portalBlock.getPortalAxisProperty()

        for (portalBlockPos in portalBlockPositions) {
            level.setBlockAndUpdate(
                portalBlockPos.toBlockPos(),
                type.portalBlock.defaultBlockState()
                    .setValue(axisProperty, axis)
            )
        }

        updatePortalStorage(level) {
            it.add(toAccess())
        }

        return true
    }

    companion object {
        const val PORTAL_SEARCH_DISTANCE = 24

        internal fun onUseItemOnBlock(itemStack: ItemStack, blockState: BlockState, level: Level, blockPos: BlockPos, player: Player, hand: InteractionHand, blockHitResult: BlockHitResult): InteractionResult? {
            val position = blockPos.toPosition3i() + blockHitResult.direction.toOffset()
            val item = itemStack.item
            val successful = ignitePortal(level, position, blockState, PortalIgnitionSource.item(item))
            if (successful) {
                itemStack.consume(1, player)
                return InteractionResult.SUCCESS
            }
            else return null
        }

        private fun ignitePortal(level: Level, position: Position3i, frameBlockState: BlockState, source: PortalIgnitionSource<*>): Boolean {
            val portalType = PortalType.getByFrameBlock(frameBlockState.block) ?: return false

            val portal: CustomPortal = portalType.portalFinder.findPortal(level, position, CustomPortal::isIgnitable)
                ?: return false

            return portal.ignite(level, source)
        }

        internal fun updatePortalStorage(level: Level, callback: (MutableList<PortalAccess>) -> Unit) {
            val attachments = level.globalAttachments()
            val attached = attachments.getAttachedOrElse(Noctiluca.PORTAL_ACCESSES, mapOf()).toMutableMap()

            val dimId = level.dimension().identifier()

            val list = attached[dimId]?.toMutableList() ?: mutableListOf()

            callback(list)

            attached[dimId] = list

            attachments.setAttached(Noctiluca.PORTAL_ACCESSES, attached)
        }
    }
}
