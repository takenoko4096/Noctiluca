package io.github.takenoko4096.noctiluca.datagen

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.datagen.providers.NoctilucaBlockEntityTypeTagsProvider
import io.github.takenoko4096.noctiluca.datagen.providers.NoctilucaBlockTagsProvider
import io.github.takenoko4096.noctiluca.datagen.providers.NoctilucaEntityTypeTagsProvider
import io.github.takenoko4096.noctiluca.datagen.providers.NoctilucaItemTagsProvider
import io.github.takenoko4096.noctiluca.datagen.providers.NoctilucaLanguageProvider
import io.github.takenoko4096.noctiluca.datagen.providers.NoctilucaModelProvider
import io.github.takenoko4096.noctiluca.datagen.providers.NoctilucaWorldGenProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import java.util.concurrent.CompletableFuture

abstract class NoctilucaDataGenerator(private val mod: NoctilucaModInitializer) : DataGeneratorEntrypoint {
    final override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()

        pack.addProvider { output: FabricPackOutput ->
            NoctilucaModelProvider(mod, output)
        }

        pack.addProvider { output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            NoctilucaLanguageProvider.EnUs(mod, output, registryLookup)
        }

        pack.addProvider { output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            NoctilucaLanguageProvider.JaJp(mod, output, registryLookup)
        }

        pack.addProvider { output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            NoctilucaItemTagsProvider(mod, output, registryLookup)
        }

        pack.addProvider { output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            NoctilucaBlockTagsProvider(mod, output, registryLookup)
        }

        pack.addProvider { output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            NoctilucaEntityTypeTagsProvider(mod, output, registryLookup)
        }

        pack.addProvider { output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            NoctilucaBlockEntityTypeTagsProvider(mod, output, registryLookup)
        }

        pack.addProvider { output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider> ->
            NoctilucaWorldGenProvider(mod, output, registryLookup)
        }

        onInitialize(pack)
    }

    override fun buildRegistry(registryBuilder: RegistrySetBuilder) {
        // registryBuilder.add(Registries.DIMENSION, NoctilucaWorldGenProvider::configureDimensionRegistry)
        // registryBuilder.add(Registries.BIOME, NoctilucaWorldGenProvider::configureBiomeRegistry)
    }

    open fun onInitialize(pack: FabricDataGenerator.Pack) {

    }
}
