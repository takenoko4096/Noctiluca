package io.github.takenoko4096.noctiluca.datagen.providers

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.fabricmc.fabric.api.biome.v1.TheEndBiomes
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Biomes
import java.util.concurrent.CompletableFuture

class NoctilucaWorldGenProvider(private val mod: NoctilucaModInitializer, output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : FabricDynamicRegistryProvider(output, registryLookup) {
    override fun configure(registryAccess: HolderLookup.Provider, entries: Entries) {
        // entries.addAll(registryAccess.lookupOrThrow(Registries.DIMENSION))
        // entries.addAll(registryAccess.lookupOrThrow(Registries.BIOME))
    }

    override fun getName(): String {
        return "NoctilucaWorldGenProvider"
    }

    companion object {
        internal fun configureDimensionRegistry(context: BootstrapContext<Level>) {

        }

        internal fun configureBiomeRegistry(context: BootstrapContext<Biome>) {

        }
    }
}
