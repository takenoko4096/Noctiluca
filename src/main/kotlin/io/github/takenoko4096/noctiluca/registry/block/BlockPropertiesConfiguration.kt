package io.github.takenoko4096.noctiluca.registry.block

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.storage.loot.LootTable
import java.util.Optional

@NoctilucaDsl
class BlockPropertiesConfiguration internal constructor(private val configuration: ModBlockConfiguration, callback: BlockPropertiesConfiguration.() -> Unit) {
    var destroyTime: Float = 1f

    var sound: SoundType = SoundType.STONE

    var occlusion = true

    var collision = true

    var friction = 0.6f

    var speedFactor = 1f

    var jumpFactor = 1f

    var pushReaction: PushReaction = PushReaction.NORMAL

    var lootTable: ResourceKey<LootTable>? = null

    var isLiquid = false

    var isReplaceable = false

    var requiresCorrectToolForDrops = false

    var explosionResistance: Float = 1f

    private var mapColorProvider: (BlockState) -> MapColor = { MapColor.STONE }

    private var lightLevelProvider: (BlockState) -> Int = { 0 }

    init {
        callback()
    }

    fun mapColor(callback: (BlockState) -> MapColor) {
        mapColorProvider = callback
    }

    fun lightLevel(callback: (BlockState) -> Int) {
        lightLevelProvider = callback
    }

    internal fun build(): BlockBehaviour.Properties {
        val properties = BlockBehaviour.Properties.of()
        properties.setId(configuration.blockResourceKey)
        properties.destroyTime(destroyTime)
        properties.sound(sound)
        if (!occlusion) properties.noOcclusion()
        if (!collision) properties.noCollision()
        if (lootTable != null) properties.overrideLootTable(Optional.of(lootTable!!))
        if (isLiquid) properties.liquid()
        if (isReplaceable) properties.replaceable()
        if (requiresCorrectToolForDrops) properties.requiresCorrectToolForDrops()
        properties.explosionResistance(explosionResistance)
        properties.friction(friction)
        properties.speedFactor(speedFactor)
        properties.jumpFactor(jumpFactor)
        properties.pushReaction(pushReaction)
        properties.mapColor(mapColorProvider)
        properties.lightLevel(lightLevelProvider)

        return properties
    }
}
