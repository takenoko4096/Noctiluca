package io.github.takenoko4096.noctiluca.registry.creativetab

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.registry.translation.ModTranslationConfiguration
import io.github.takenoko4096.noctiluca.item.ItemComponents
import io.github.takenoko4096.noctiluca.item.ItemStackBuilder
import io.github.takenoko4096.noctiluca.text.component
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block

@NoctilucaDsl
class ModCreativeModeTabConfiguration internal constructor(private val mod: NoctilucaModInitializer, private val name: String, callback: ModCreativeModeTabConfiguration.() -> Unit) {
    val resourceKey: ResourceKey<CreativeModeTab> = ResourceKey.create(
        Registries.CREATIVE_MODE_TAB,
        mod.identifierOf(name)
    )

    private var icon: ItemStackBuilder? = null

    private var translationKey: String? = null

    private var translation: ModTranslationConfiguration? = null

    private var displayConfigurationCallback: (DisplayConfiguration.() -> Unit)? = null

    init {
        callback()
    }

    fun icon(item: Item, amount: Int = 1, callback: ItemComponents.() -> Unit = {}) {
        icon = ItemStackBuilder(item, amount, callback)
    }

    fun translationKeyOf(name: String) {
        translationKey = "itemGroup.${mod.identifier}.$name"
    }

    fun translationKeyAuto() {
        translationKey = "itemGroup.${mod.identifier}.$name"
    }

    fun translation(callback: ModTranslationConfiguration.() -> Unit) {
        translation = ModTranslationConfiguration()
        translation!!.callback()
    }

    fun items(callback: DisplayConfiguration.() -> Unit) {
        displayConfigurationCallback = callback
    }

    fun register(): CreativeModeTab {
        if (translationKey == null) {
            translationKeyAuto()
        }
        else if (translation == null) {
            throw IllegalStateException("クリエイティブモードタブを作成できません: 翻訳が設定されていません")
        }
        else if (icon == null) {
            throw IllegalStateException("クリエイティブモードタブを作成できません: アイコンが設定されていません")
        }
        else if (displayConfigurationCallback == null) {
            throw IllegalStateException("クリエイティブモードタブを作成できません: 表示アイテムが設定されていません")
        }

        val trk = translationKey!!

        val tab = FabricCreativeModeTab.builder()
            .title(component {
                translate(trk)
            })
            .icon { icon!!.build(mod) }
            .displayItems { parameters, output ->
                DisplayConfiguration(this, parameters, output, displayConfigurationCallback!!)
            }
            .build()

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, resourceKey, tab)

        return tab
    }

    @NoctilucaDsl
    class DisplayConfiguration internal constructor(private val parent: ModCreativeModeTabConfiguration, private val parameters: CreativeModeTab.ItemDisplayParameters, private val output: CreativeModeTab.Output, private val callback: DisplayConfiguration.() -> Unit) {
        private var itemLikes = mutableSetOf<ItemLike>()

        private var itemStacks = mutableSetOf<ItemStack>()

        private val registryAccess: HolderLookup.Provider = parameters.holders

        init {
            callback()

            for (item in itemLikes) {
                output.accept(item)
            }

            for (item in itemStacks) {
                output.accept(item)
            }
        }

        fun item(item: Item) {
            itemLikes.add(item)
        }

        fun block(block: Block) {
            itemLikes.add(block)
        }

        fun itemStack(item: Item, amount: Int = 1, components: ItemComponents.() -> Unit) {
            itemStacks.add(ItemStackBuilder(item, amount, components).build(parent.mod, registryAccess))
        }
    }

    class Accessor internal constructor(private val configuration: ModCreativeModeTabConfiguration) {
        fun translation(): ModTranslationConfiguration {
            return configuration.translation!!
        }

        fun translationKey(): String {
            return configuration.translationKey!!
        }
    }

    companion object {
        fun getAccessor(configuration: ModCreativeModeTabConfiguration): Accessor {
            return Accessor(configuration)
        }
    }
}
