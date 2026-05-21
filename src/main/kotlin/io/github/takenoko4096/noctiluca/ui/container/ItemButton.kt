package io.github.takenoko4096.noctiluca.ui.container

import io.github.takenoko4096.mojangson.MojangsonPath
import io.github.takenoko4096.mojangson.MojangsonValueTypes
import io.github.takenoko4096.noctiluca.Noctiluca
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.item.ItemComponents
import io.github.takenoko4096.noctiluca.item.ItemStackBuilder
import io.github.takenoko4096.noctiluca.nbt.toMojangsonCompound
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class ItemButton private constructor(val id: Int, private val itemStackBuilder: ItemStackBuilder, private val onClick: ItemButtonClickEvent.() -> Unit) : ItemButtonProvider() {
    internal fun itemStack(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider): ItemStack {
        return itemStackBuilder.build(mod, registryAccess)
    }

    internal fun click(event: ItemButtonClickEvent) {
        event.onClick()
    }

    override fun getButton(): ItemButton {
        return this
    }

    @NoctilucaDsl
    class ItemButtonConfiguration internal constructor(private val item: Item, private val amount: Int = 1, private val callback: ItemButtonConfiguration.() -> Unit) {
        private val id = itemButtonMaximumId++

        private var itemStackBuilder: ItemStackBuilder = ItemStackBuilder(item, amount) {
            customData {
                compound[ITEM_BUTTON_ID_PATH] = this@ItemButtonConfiguration.id
            }
        }

        private var onClick: ItemButtonClickEvent.() -> Unit = {}

        init {
            callback()
        }

        fun clone(): ItemButtonProvider {
            return object : ItemButtonProvider() {
                override fun getButton(): ItemButton {
                    return ItemButtonConfiguration(item, amount, callback).build()
                }
            }
        }

        fun components(components: ItemComponents.() -> Unit) {
            itemStackBuilder = ItemStackBuilder(item, amount) {
                components()

                customData {
                    compound[ITEM_BUTTON_ID_PATH] = this@ItemButtonConfiguration.id
                }
            }
        }

        fun onClick(callback: ItemButtonClickEvent.() -> Unit) {
            onClick = callback
        }

        internal fun build(): ItemButton {
            return ItemButton(id, itemStackBuilder, onClick)
        }
    }

    companion object {
        val ITEM_BUTTON_ID_PATH = MojangsonPath.of("${Noctiluca.identifier}.ui.container.button.id")

        private var itemButtonMaximumId = -2147483648

        fun isButton(itemStack: ItemStack): Boolean {
            val customData = itemStack.components[DataComponents.CUSTOM_DATA] ?: return false
            val compound = customData.copyTag().toMojangsonCompound()
            if (!compound.has(ITEM_BUTTON_ID_PATH)) return false
            return compound.getTypeOf(ITEM_BUTTON_ID_PATH) == MojangsonValueTypes.INT
        }

        fun of(item: Item, amount: Int = 1, configuration: ItemButtonConfiguration.() -> Unit): ItemButton {
            return ItemButtonConfiguration(item, amount, configuration).build()
        }
    }
}
