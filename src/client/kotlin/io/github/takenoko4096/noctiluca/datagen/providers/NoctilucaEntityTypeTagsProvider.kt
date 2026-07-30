package io.github.takenoko4096.noctiluca.datagen.providers

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import java.util.concurrent.CompletableFuture

class NoctilucaEntityTypeTagsProvider internal constructor(val mod: NoctilucaModInitializer, output: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : FabricTagsProvider.EntityTypeTagsProvider(output, registryLookup) {
    override fun addTags(p0: HolderLookup.Provider) {
        val registry = mod.tagRegistry
        for (configuration in registry.getConfigurations(Registries.ENTITY_TYPE)) {
            val tag = registry.getTag(configuration.key)

            val tagAppender = tag(tag.tagKey)
            tagAppender.add(*tag.entries.toTypedArray())
            tagAppender.setReplace(tag.replace)
        }
    }
}