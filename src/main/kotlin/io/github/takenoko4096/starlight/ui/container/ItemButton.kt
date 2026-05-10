package io.github.takenoko4096.starlight.ui.container

import io.github.takenoko4096.mojangson.MojangsonPath
import io.github.takenoko4096.mojangson.MojangsonValueTypes
import io.github.takenoko4096.starlight.Noctiluca
import io.github.takenoko4096.starlight.NoctilucaModInitializer
import io.github.takenoko4096.starlight.item.ItemComponents
import io.github.takenoko4096.starlight.item.ItemStackBuilder
import io.github.takenoko4096.starlight.nbt.toMojangsonCompound
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class ItemButton private constructor(val id: Int, private val itemStackBuilder: ItemStackBuilder, private val onClick: ItemButtonClickEvent.() -> Unit) {
    internal fun itemStack(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider): ItemStack {
        return itemStackBuilder.build(mod, registryAccess)
    }

    internal fun click(event: ItemButtonClickEvent) {
        event.onClick()
    }

    class ItemButtonConfiguration internal constructor(private val item: Item, private val amount: Int = 1, callback: ItemButtonConfiguration.() -> Unit) {
        private val id = itemButtonMaximumId++

        private var itemStackBuilder: ItemStackBuilder = ItemStackBuilder(item, amount)

        private var onClick: ItemButtonClickEvent.() -> Unit = {}

        init {
            callback()
        }

        fun components(callback: ItemComponents.() -> Unit) {
            itemStackBuilder = ItemStackBuilder(item, amount) {
                callback()

                customData {
                    compound.set()
                    compound[ITEM_BUTTON_ID_PATH] = id
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

    class ItemButtonClickEvent internal constructor(val interaction: ContainerInteraction, val player: Player, val button: ItemButton)

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
