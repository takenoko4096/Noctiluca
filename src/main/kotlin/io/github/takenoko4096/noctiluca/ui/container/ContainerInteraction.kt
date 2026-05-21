package io.github.takenoko4096.noctiluca.ui.container

import io.github.takenoko4096.noctiluca.Noctiluca
import io.github.takenoko4096.noctiluca.container.CustomContainerMenu
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

open class ContainerInteraction(callback: ContainerInteractionConfiguration.() -> Unit) {
    var title: Component

    internal val contents: ContainerInteractiveContents

    internal val children = mutableSetOf<ContainerInteractionMenuProvider.Open>()

    init {
        val configuration = ContainerInteractionConfiguration(callback)
        title = configuration.title
        contents = configuration.contents
    }

    operator fun get(index: Int): ItemButton? {
        return contents.buttons[index]?.getButton()
    }

    open operator fun set(slot: Int, button: ItemButtonProvider) {
        if (slot !in 0..<(CustomContainerMenu.SLOTS_PER_ROW * contents.columnCount)) {
            throw IllegalArgumentException("cannot set button to slot $slot: slot index is out of bounds")
        }

        contents.buttons[slot] = button
        for ((player, menu) in children) {
            menu.slots[slot].set(button.getButton().itemStack(Noctiluca, player.registryAccess()))
        }
    }

    fun append(button: ItemButtonProvider) {
        for (i in 0..<(CustomContainerMenu.SLOTS_PER_ROW * contents.columnCount)) {
            if (i !in contents.buttons) {
                set(i, button)
                for ((player, menu) in children) {
                    menu.slots[i].set(button.getButton().itemStack(Noctiluca, player.registryAccess()))
                }
                return
            }
        }

        throw IllegalArgumentException("cannot add button: container is full")
    }

    fun fillRow(rowIndex: Int, button: ItemButtonProvider) {
        val start = rowIndex * CustomContainerMenu.SLOTS_PER_ROW
        for (i in 0..<CustomContainerMenu.SLOTS_PER_ROW) {
            set(start + i, button)
        }
    }

    fun fillColumn(columnIndex: Int, button: ItemButtonProvider) {
        for (i in 0..<contents.columnCount) {
            set(i * CustomContainerMenu.SLOTS_PER_ROW + columnIndex, button)
        }
    }

    fun open(player: Player) {
        // もう使用されていないmenuを解放
        children.removeIf { (player, menu) -> player.containerMenu != menu }

        val provider = ContainerInteractionMenuProvider(
            this,
            title,
            contents.columnCount,
            contents.toInitializer(Noctiluca, player.registryAccess())
        ) { player, slot, button, input, slots ->
            val button = get(slot) ?: return@ContainerInteractionMenuProvider false
            button.click(ItemButtonClickEvent(this, player, button, slots[slot].item.copy()))
            return@ContainerInteractionMenuProvider false
        }

        player.openMenu(provider)
    }
}
