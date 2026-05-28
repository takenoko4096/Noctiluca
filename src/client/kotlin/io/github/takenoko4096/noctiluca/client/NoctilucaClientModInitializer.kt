package io.github.takenoko4096.noctiluca.client

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.client.ui.container.CustomContainerScreen
import io.github.takenoko4096.noctiluca.registry.block.ModBlockConfiguration
import io.github.takenoko4096.noctiluca.registry.command.node.ConfigurableCommandNode
import io.github.takenoko4096.noctiluca.container.CustomContainerMenu
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.color.block.BlockTintSource
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.commands.CommandBuildContext
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

abstract class NoctilucaClientModInitializer(private val mod: NoctilucaModInitializer) : ClientModInitializer {
    private val commands = mutableSetOf<(CommandBuildContext) -> LiteralArgumentBuilder<FabricClientCommandSource>>()

    final override fun onInitializeClient() {
        val blockRegistry = mod.blockRegistry

        for (configuration in blockRegistry.getConfigurations()) {
            val accessor = ModBlockConfiguration.getAccessorForClient(configuration)
            val block = blockRegistry.getBlock(configuration.blockResourceKey)

            // tint
            BlockColorRegistry.register(
                listOf(object : BlockTintSource {
                    override fun color(state: BlockState): Int {
                        return accessor.defaultTint()(state)
                    }

                    override fun colorInWorld(state: BlockState, level: BlockAndTintGetter, pos: BlockPos): Int {
                        return if (level is ClientLevel) {
                            accessor.inWorldTint()(state, pos, level)
                        }
                        else {
                            super.colorInWorld(state, level, pos)
                        }
                    }

                    override fun colorAsTerrainParticle(state: BlockState, level: BlockAndTintGetter, pos: BlockPos): Int {
                        return if (level is ClientLevel) {
                            accessor.terrainParticleTint()(state, pos, level)
                        }
                        else {
                            super.colorAsTerrainParticle(state, level, pos)
                        }
                    }
                }),
                block
            )
        }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, registryAccess ->
            commands.forEach {
                val command = it(registryAccess)
                dispatcher.register(command)
            }
        }

        onInitialize()

        ClientLifecycleEvents.CLIENT_STARTED.register(::onClientStart)
    }

    fun registerClientCommand(name: String, callback: ConfigurableCommandNode<FabricClientCommandSource>.() -> Unit) {
        commands.add {
            ConfigurableCommandNode(it, name, callback).build()
        }
    }

    open fun onInitialize() {

    }

    open fun onClientStart(client: Minecraft) {}
}
