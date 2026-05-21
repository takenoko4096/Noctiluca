package io.github.takenoko4096.noctiluca.registry.tag

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.registry.StarlightRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType

class ModTagRegistry internal constructor(mod: NoctilucaModInitializer) : StarlightRegistry(mod) {
    private val configurations = mutableSetOf<ModTagConfiguration<*>>()

    private val tags = mutableMapOf<TagKey<*>, Tag<*>>()

    fun registerOfItem(tag: TagKey<Item>, configuration: ModTagConfiguration<Item>.() -> Unit) {
        val configuration = ModTagConfiguration(Registries.ITEM, tag, configuration)
        configurations.add(configuration)

        val built = configuration.build()

        if (tag in tags) {
            val previous = tags[tag] as Tag<Item>

            if (previous.replace != built.replace) {
                throw IllegalStateException("タグ '${tag.location}' の定義で 'replace' の設定が競合しています")
            }

            tags[tag] = Tag(
                Registries.ITEM,
                tag,
                previous.entries + built.entries,
                replace = false
            )
        }
        else {
            tags[tag] = built
        }
    }

    fun registerOfBlock(tag: TagKey<Block>, configuration: ModTagConfiguration<Block>.() -> Unit) {
        val configuration = ModTagConfiguration(Registries.BLOCK, tag, configuration)
        configurations.add(configuration)

        val built = configuration.build()

        if (tag in tags) {
            val previous = tags[tag] as Tag<Block>

            if (previous.replace != built.replace) {
                throw IllegalStateException("タグ '${tag.location}' の定義で 'replace' の設定が競合しています")
            }

            tags[tag] = Tag(
                Registries.BLOCK,
                tag,
                previous.entries + built.entries,
                replace = false
            )
        }
        else {
            tags[tag] = built
        }
    }

    fun registerOfEntityType(tag: TagKey<EntityType<*>>, configuration: ModTagConfiguration<EntityType<*>>.() -> Unit) {
        val configuration = ModTagConfiguration(Registries.ENTITY_TYPE, tag, configuration)
        configurations.add(configuration)

        val built = configuration.build()

        if (tag in tags) {
            val previous = tags[tag] as Tag<EntityType<*>>

            if (previous.replace != built.replace) {
                throw IllegalStateException("タグ '${tag.location}' の定義で 'replace' の設定が競合しています")
            }

            tags[tag] = Tag(
                Registries.ENTITY_TYPE,
                tag,
                previous.entries + built.entries,
                replace = false
            )
        }
        else {
            tags[tag] = built
        }
    }

    fun registerOfBlockEntityType(tag: TagKey<BlockEntityType<*>>, configuration: ModTagConfiguration<BlockEntityType<*>>.() -> Unit) {
        val configuration = ModTagConfiguration(Registries.BLOCK_ENTITY_TYPE, tag, configuration)
        configurations.add(configuration)

        val built = configuration.build()

        if (tag in tags) {
            val previous = tags[tag] as Tag<BlockEntityType<*>>

            if (previous.replace != built.replace) {
                throw IllegalStateException("タグ '${tag.location}' の定義で 'replace' の設定が競合しています")
            }

            tags[tag] = Tag(
                Registries.BLOCK_ENTITY_TYPE,
                tag,
                previous.entries + built.entries,
                replace = false
            )
        }
        else {
            tags[tag] = built
        }
    }

    fun <T : Any> getConfigurations(target: ResourceKey<Registry<T>>): Set<ModTagConfiguration<T>> {
        return configurations.filter { it.target == target }.map { it as ModTagConfiguration<T> }.toSet()
    }

    fun <T : Any> getTag(tag: TagKey<T>): Tag<T> {
        return tags[tag] as Tag<T>? ?: throw IllegalArgumentException("タグ '$tag' がレジストリに見つかりませんでした")
    }
}
