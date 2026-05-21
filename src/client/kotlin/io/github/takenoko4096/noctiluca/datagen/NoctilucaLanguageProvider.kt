package io.github.takenoko4096.noctiluca.datagen

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.registry.block.ModBlockConfiguration
import io.github.takenoko4096.noctiluca.registry.creativetab.ModCreativeModeTabConfiguration
import io.github.takenoko4096.noctiluca.registry.item.ModItemConfiguration
import io.github.takenoko4096.noctiluca.registry.translation.ModTranslationConfiguration
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture
import kotlin.collections.iterator

abstract class NoctilucaLanguageProvider(private val mod: NoctilucaModInitializer, output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider>, private val languageCode: String) : FabricLanguageProvider(output, languageCode, registryLookup) {
    protected abstract fun getTranslation(translationConfiguration: ModTranslationConfiguration): String?

    override fun generateTranslations(holderLookupProvider: HolderLookup.Provider, translationBuilder: TranslationBuilder) {
        val blockRegistry = mod.blockRegistry
        for (configuration in blockRegistry.getConfigurations()) {
            val block = blockRegistry.getBlock(configuration.blockResourceKey)
            val accessor = ModBlockConfiguration.getAccessorForClient(configuration)
            val translation = accessor.translation()

            getTranslation(translation)?.let {
                translationBuilder.add(block.descriptionId, it)
            }
        }

        val itemRegistry = mod.itemRegistry
        for (configuration in itemRegistry.getConfigurations()) {
            val item = itemRegistry.getItem(configuration.itemResourceKey)
            val accessor = ModItemConfiguration.getAccessor(configuration)
            val translation = accessor.translation()

            getTranslation(translation)?.let {
                translationBuilder.add(item.descriptionId, it)
            }
        }

        val creativeModeTabRegistry = mod.creativeModeTabRegistry
        for (configuration in creativeModeTabRegistry.getConfigurations()) {
            val accessor = ModCreativeModeTabConfiguration.getAccessor(configuration)
            val translation = accessor.translation()

            getTranslation(translation)?.let {
                translationBuilder.add(accessor.translationKey(), it)
            }
        }

        val translationRegistry = mod.translationRegistry
        for (translation in translationRegistry.getTranslations()) {
            getTranslation(translation.value)?.let {
                translationBuilder.add(translation.key, it)
            }
        }
    }

    override fun getName(): String {
        return "StarlightLanguageProvider_$languageCode"
    }

    class EnUs(mod: NoctilucaModInitializer, output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : NoctilucaLanguageProvider(mod, output, registryLookup, "en_us") {
        override fun getTranslation(translationConfiguration: ModTranslationConfiguration): String? {
            return translationConfiguration.enUs
        }
    }

    class JaJp(mod: NoctilucaModInitializer, output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : NoctilucaLanguageProvider(mod, output, registryLookup, "ja_jp") {
        override fun getTranslation(translationConfiguration: ModTranslationConfiguration): String? {
            return translationConfiguration.jaJp
        }
    }
}
