package io.github.takenoko4096.noctiluca.ui.dialog

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.item.ItemComponents
import io.github.takenoko4096.noctiluca.item.ItemStackBuilder
import io.github.takenoko4096.noctiluca.text.SectionComponentBuilder
import io.github.takenoko4096.noctiluca.text.component
import net.minecraft.core.HolderLookup
import net.minecraft.network.chat.Component
import net.minecraft.server.dialog.body.DialogBody
import net.minecraft.server.dialog.body.ItemBody
import net.minecraft.server.dialog.body.PlainMessage
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStackTemplate
import java.util.Optional

@NoctilucaDsl
class DialogBodyConfiguration internal constructor(callback: DialogBodyConfiguration.() -> Unit) {
    private val contents = mutableListOf<DialogBodyContent<*>>()

    init {
        callback()
    }

    fun message(callback: PlainMessageConfiguration.() -> Unit) {
        contents.add(PlainMessageConfiguration(callback).build())
    }

    fun item(item: Item, amount: Int = 1, callback: ItemConfiguration.() -> Unit) {
        contents.add(ItemConfiguration(item, amount, callback).build())
    }

    internal fun build(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider): List<DialogBody> {
        return contents.map { it.build(mod, registryAccess) }
    }

    @NoctilucaDsl
    class PlainMessageConfiguration(callback: PlainMessageConfiguration.() -> Unit) {
        private var contents: Component = Component.empty()

        var width: Int = 200

        init {
            callback()
        }

        fun contents(callback: SectionComponentBuilder.() -> Unit) {
            contents = component(callback)
        }

        internal fun build(): DialogBodyContent.PlainMessageBodyContent {
            return DialogBodyContent.PlainMessageBodyContent(
                contents,
                width
            )
        }
    }

    @NoctilucaDsl
    class ItemConfiguration(private val item: Item, private val amount: Int = 1, callback: ItemConfiguration.() -> Unit) {
        private var components: ItemComponents.() -> Unit = {}

        private var description: DialogBodyContent.PlainMessageBodyContent? = null

        var showTooltip: Boolean = true

        var width: Int = 16

        var height: Int = 16

        init {
            callback()
        }

        fun components(callback: ItemComponents.() -> Unit) {
            components = callback
        }

        fun description(callback: PlainMessageConfiguration.() -> Unit) {
            description = PlainMessageConfiguration(callback).build()
        }

        internal fun build(): DialogBodyContent.ItemBodyContent {
            return DialogBodyContent.ItemBodyContent(
                ItemStackBuilder(item, amount, components),
                description,
                showTooltip,
                width,
                height
            )
        }
    }

    abstract class DialogBodyContent<T : DialogBody> protected constructor() {
        abstract fun build(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider): T

        class PlainMessageBodyContent internal constructor(private val component: Component, private val width: Int) : DialogBodyContent<PlainMessage>() {
            override fun build(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider): PlainMessage {
                return PlainMessage(component, width)
            }
        }

        class ItemBodyContent internal constructor(private val itemStackBuilder: ItemStackBuilder, private val description: PlainMessageBodyContent?, private val showTooltip: Boolean, private val width: Int, private val height: Int) : DialogBodyContent<ItemBody>() {
            override fun build(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider): ItemBody {
                return ItemBody(
                    ItemStackTemplate.fromNonEmptyStack(itemStackBuilder.build(mod, registryAccess)),
                    description?.let { Optional.of(it.build(mod, registryAccess)) } ?: Optional.empty(),
                    true,
                    showTooltip,
                    width,
                    height
                )
            }
        }
    }
}
