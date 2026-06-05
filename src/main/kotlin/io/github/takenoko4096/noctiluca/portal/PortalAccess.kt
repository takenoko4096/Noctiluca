package io.github.takenoko4096.noctiluca.portal

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.takenoko4096.noctiluca.math.Position3i
import net.minecraft.resources.Identifier
import net.minecraft.world.level.Level

data class PortalAccess(val type: Identifier, val position: Position3i, val axis: PortalAxis) {
    fun getPortal(level: Level): CustomPortal? {
        return PortalType.get(type)?.portalFinder?.findPortalWithAxis(level, position, axis) { isCompletePortal() }
    }

    companion object {
        val CODEC: Codec<PortalAccess> = RecordCodecBuilder.create {
            it.group(
                Identifier.CODEC.fieldOf("type").forGetter(PortalAccess::type),
                Position3i.CODEC.fieldOf("pos").forGetter(PortalAccess::position),
                PortalAxis.CODEC.fieldOf("axis").forGetter(PortalAccess::axis)
            ).apply(it, ::PortalAccess)
        }
    }
}
