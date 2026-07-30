package io.github.takenoko4096.noctiluca.registry.item.templates

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.math.toPosition3i
import io.github.takenoko4096.noctiluca.registry.block.CustomFireBlock
import io.github.takenoko4096.noctiluca.registry.block.templates.ModBlockTemplate
import io.github.takenoko4096.noctiluca.registry.item.CustomItem
import io.github.takenoko4096.noctiluca.registry.item.ItemEventsConfiguration
import io.github.takenoko4096.noctiluca.registry.item.ModItemConfiguration
import net.minecraft.advancements.triggers.CriteriaTriggers
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseFireBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CampfireBlock
import net.minecraft.world.level.block.CandleBlock
import net.minecraft.world.level.block.CandleCakeBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.gameevent.GameEvent

@NoctilucaDsl
class FlintAndSteelItemTemplate(callback: FlintAndSteelItemTemplate.() -> Unit) : ModItemTemplate<CustomItem>() {
    var durability: Int? = null

    private var fire: Block = Blocks.FIRE

    private var sound: ModBlockTemplate.AmbientConfiguration.SoundConfiguration = ModBlockTemplate.AmbientConfiguration.SoundConfiguration {}

    init {
        callback()
    }

    fun fire(block: Block) {
        fire = block
    }

    fun sound(callback: ModBlockTemplate.AmbientConfiguration.SoundConfiguration.() -> Unit) {
        sound = ModBlockTemplate.AmbientConfiguration.SoundConfiguration(callback)
    }

    private fun playSound(level: Level, player: Player?, pos: BlockPos) {
        level.playSound(
            player,
            pos,
            sound.soundEvent,
            SoundSource.BLOCKS,
            ModBlockTemplate.AmbientConfiguration.SoundValueProvider(
                level,
                pos.toPosition3i(),
                level.random,
                sound.volume ?: { 1.0f }
            ).value,
            ModBlockTemplate.AmbientConfiguration.SoundValueProvider(
                level,
                pos.toPosition3i(),
                level.random,
                sound.pitch ?: { randomSource.nextFloat() * 0.4f + 0.8f }
            ).value
        )
    }

    private fun light(event: ItemEventsConfiguration.InteractBlockEvent): InteractionResult {
        val level = event.useOnContext.level
        val pos = event.useOnContext.clickedPos
        val blockState = level.getBlockState(event.useOnContext.clickedPos)
        val player: Player? = event.useOnContext.player

        playSound(level, player, pos)

        level.setBlock(pos, blockState.setValue(BlockStateProperties.LIT, true), Block.UPDATE_ALL_IMMEDIATE)
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)

        if (player != null) {
            event.useOnContext.itemInHand.hurtAndBreak(1, player, event.useOnContext.hand.asEquipmentSlot())
        }

        return InteractionResult.SUCCESS
    }

    private fun ignite(event: ItemEventsConfiguration.InteractBlockEvent, targetPos: BlockPos): InteractionResult {
        val level = event.useOnContext.level
        val pos = event.useOnContext.clickedPos
        val player: Player? = event.useOnContext.player

        playSound(level, player, pos)

        val fireState = when (val fireBlock = fire) {
            is BaseFireBlock -> BaseFireBlock.getState(level, targetPos)
            is CustomFireBlock -> fireBlock.getBlockStateAt(level, targetPos)
            is FireBlockStateGettable -> fireBlock.getFireBlockState(level, targetPos)
            else -> fireBlock.defaultBlockState()
        }

        level.setBlock(targetPos, fireState, Block.UPDATE_ALL_IMMEDIATE)
        level.gameEvent(player, GameEvent.BLOCK_PLACE, pos)

        val itemStack: ItemStack = event.useOnContext.itemInHand
        if (player is ServerPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger(player, targetPos, itemStack)
            itemStack.hurtAndBreak(1, player, event.useOnContext.hand.asEquipmentSlot())
        }

        return InteractionResult.SUCCESS
    }

    private fun useOn(event: ItemEventsConfiguration.InteractBlockEvent) {
        val pos = event.useOnContext.clickedPos
        val level = event.useOnContext.level
        val blockState = level.getBlockState(event.useOnContext.clickedPos)

        if (CampfireBlock.canLight(blockState) || CandleBlock.canLight(blockState) || CandleCakeBlock.canLight(blockState)) {
            event.interactionResult = light(event)
            return
        }

        val targetPos = pos.relative(event.useOnContext.clickedFace)
        if (BaseFireBlock.canBePlacedAt(level, targetPos, event.useOnContext.horizontalDirection)) {
            event.interactionResult = ignite(event, targetPos)
            return
        }

        event.interactionResult = InteractionResult.FAIL
    }

    override fun getConfigurator(identifier: Identifier): ModItemConfiguration.() -> Unit = {
        itemProperties {
            translationKeyAuto()

            components {
                val boundDurability = this@FlintAndSteelItemTemplate.durability
                if (boundDurability != null) {
                    maxDamage(boundDurability)
                }

                damage(0)

                maxStackSize(1)
            }
        }

        val boundModelBuilder = model
        if (boundModelBuilder != null) {
            model(boundModelBuilder)
        }

        events {
            onUseOn(this@FlintAndSteelItemTemplate::useOn)
        }

        translation(this@FlintAndSteelItemTemplate.translation)
    }

    interface FireBlockStateGettable {
        fun getFireBlockState(level: Level, blockPos: BlockPos): BlockState
    }
}
