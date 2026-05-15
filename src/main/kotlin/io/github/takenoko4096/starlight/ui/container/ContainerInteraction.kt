package io.github.takenoko4096.starlight.ui.container

import io.github.takenoko4096.starlight.Noctiluca
import io.github.takenoko4096.starlight.container.CustomContainerMenu
import io.github.takenoko4096.starlight.container.CustomContainerMenuProvider
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

open class ContainerInteraction internal constructor(var title: Component, private val contents: ContainerInteractionConfiguration.Contents) {
    internal val children = mutableSetOf<CustomContainerMenuProvider.ContainerUse>()

    operator fun get(index: Int): ItemButton? {
        return contents.buttons[index]
    }

    open operator fun set(slot: Int, button: ItemButton) {
        if (slot !in 0..<(CustomContainerMenu.SLOTS_PER_ROW * contents.columns)) {
            throw IllegalArgumentException("cannot set button to slot $slot: slot index is out of bounds")
        }

        contents.buttons[slot] = button
        for ((player, menu) in children) {
            menu.slots[slot].set(button.itemStack(Noctiluca, player.registryAccess()))
        }
    }

    fun add(button: ItemButton) {
        for (i in 0..<(CustomContainerMenu.SLOTS_PER_ROW * contents.columns)) {
            if (i !in contents.buttons) {
                set(i, button)
                for ((player, menu) in children) {
                    menu.slots[i].set(button.itemStack(Noctiluca, player.registryAccess()))
                }
                return
            }
        }

        throw IllegalArgumentException("cannot add button: container is full")
    }

    fun open(player: Player) {
        // もう使用されていないmenuを解放
        children.removeIf { (player, menu) -> player.containerMenu != menu }

        val provider = CustomContainerMenuProvider(this, title, contents.columns, contents.toContainerInitializer(Noctiluca, player.registryAccess())) { player, slot, button, input, slots ->
            val button = get(slot) ?: return@CustomContainerMenuProvider
            button.click(ItemButton.ItemButtonClickEvent(this, player, button))
        }

        player.openMenu(provider)
    }

    companion object {
        fun create(callback: ContainerInteractionConfiguration.() -> Unit): ContainerInteraction {
            return ContainerInteractionConfiguration(callback).build()
        }
    }
}
