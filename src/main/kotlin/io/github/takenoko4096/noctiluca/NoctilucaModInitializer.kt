package io.github.takenoko4096.noctiluca

import io.github.takenoko4096.noctiluca.registry.block.ModBlockRegistry
import io.github.takenoko4096.noctiluca.registry.command.ModCommandRegistry
import io.github.takenoko4096.noctiluca.registry.creativetab.ModCreativeModeTabRegistry
import io.github.takenoko4096.noctiluca.registry.item.ModItemRegistry
import io.github.takenoko4096.noctiluca.registry.tag.ModTagRegistry
import io.github.takenoko4096.noctiluca.registry.translation.ModTranslationRegistry
import io.github.takenoko4096.noctiluca.render.TexturePath
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

abstract class NoctilucaModInitializer(val identifier: String) : ModInitializer {
    val logger: Logger = LoggerFactory.getLogger(identifier)

    val itemRegistry: ModItemRegistry = ModItemRegistry(this)

    val blockRegistry: ModBlockRegistry = ModBlockRegistry(this)

    val translationRegistry: ModTranslationRegistry = ModTranslationRegistry(this)

    val commandRegistry: ModCommandRegistry = ModCommandRegistry(this)

    val tagRegistry: ModTagRegistry = ModTagRegistry(this)

    val creativeModeTabRegistry: ModCreativeModeTabRegistry = ModCreativeModeTabRegistry(this)

    init {
        logger.info("$identifier is powered by noctiluca v. ${BuildConfig.NOCTILUCA_VERSION}")

        ServerLifecycleEvents.SERVER_STARTED.register {
            val data = ServerContainer(this, it)
            onServerStart(data)
        }
    }

    abstract override fun onInitialize()

    open fun onServerStart(data: ServerContainer) {}

    fun debugger(name: String, callback: Debugger.DebuggerCallable) {
        Debugger.register(identifierOf(name), callback)
    }

    override fun hashCode(): Int {
        return Objects.hash(identifier)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is NoctilucaModInitializer) identifier == other.identifier else false
    }

    fun identifierOf(value: String): Identifier {
        return Identifier.fromNamespaceAndPath(identifier, value)
    }

    fun texturePathOf(value: String): TexturePath {
        return TexturePath(identifierOf(value))
    }
}
