package io.github.takenoko4096.noctiluca.datagen

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

abstract class NoctilucaDataGenerator(private val mod: NoctilucaModInitializer) : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
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

        onInitialize(pack)
    }

    open fun onInitialize(pack: FabricDataGenerator.Pack) {

    }
}
