package io.github.takenoko4096.starlight.ui.container

import io.github.takenoko4096.starlight.Noctiluca
import io.github.takenoko4096.starlight.container.CustomContainerMenu
import io.github.takenoko4096.starlight.container.CustomContainerMenuBuilder
import io.github.takenoko4096.starlight.container.CustomContainerMenuProvider
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

open class ContainerInteraction internal constructor(var title: Component, private val contents: ContainerInteractionConfiguration.Contents) {
    fun buttonAt(index: Int): ItemButton? {
        return contents.buttons[index]
    }

    open fun set(slot: Int, button: ItemButton) {
        if (slot !in 0..<(CustomContainerMenu.SLOTS_PER_ROW * contents.columns)) {
            throw IllegalArgumentException("cannot set button to slot $slot: slot index is out of bounds")
        }

        contents.buttons[slot] = button
    }

    fun add(button: ItemButton) {
        for (i in 0..<(CustomContainerMenu.SLOTS_PER_ROW * contents.columns)) {
            if (i !in contents.buttons) {
                set(i, button)
                return
            }
        }

        throw IllegalArgumentException("cannot add button: container is full")
    }

    fun expand(newColumnCount: Int) {
        if (newColumnCount !in 1..6) {
            throw IllegalArgumentException("cannot expand columns: new count is out of bounds")
        }

        contents.columns = newColumnCount
    }

    fun open(player: Player) {
        val provider = CustomContainerMenuProvider(title, contents.columns, contents.toContainerInitializer(Noctiluca, player.registryAccess())) { player, slot, button, input, slots ->
            val button = buttonAt(slot) ?: return@CustomContainerMenuProvider
            button.click(ItemButton.ItemButtonClickEvent(
                object : ContainerInteraction(title, contents) {
                    override fun set(slot: Int, button: ItemButton) {
                        super.set(slot, button)
                        slots[slot].set(button.itemStack(Noctiluca, player.registryAccess()))
                    }
                },
                player,
                button
            ))
        }

        player.openMenu(provider)
    }

    companion object {
        fun create(callback: ContainerInteractionConfiguration.() -> Unit): ContainerInteraction {
            return ContainerInteractionConfiguration(callback).build()
        }
    }
}
