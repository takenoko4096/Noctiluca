package io.github.takenoko4096.starlight.ui.container

import io.github.takenoko4096.starlight.NoctilucaModInitializer
import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.container.CustomContainerMenu
import net.minecraft.core.HolderLookup
import net.minecraft.world.SimpleContainer
import kotlin.collections.iterator

@StarlightDSL
class ContainerInteractionContents(internal var columns: Int, callback: ContainerInteractionContents.() -> Unit) {
    internal val buttons = mutableMapOf<Int, ItemButtonProvider>()

    init {
        callback()
    }

    fun set(slot: Int, button: ItemButton) {
        if (slot !in 0..<(CustomContainerMenu.SLOTS_PER_ROW * columns)) {
            throw IllegalArgumentException("cannot set button to slot $slot: slot index is out of bounds")
        }

        buttons[slot] = button
    }

    fun add(button: ItemButton) {
        for (i in 0..<(CustomContainerMenu.SLOTS_PER_ROW * columns)) {
            if (i !in buttons) {
                set(i, button)
                return
            }
        }

        throw IllegalArgumentException("cannot add button: container is full")
    }

    fun fillRow(rowIndex: Int, button: ItemButton) {
        val start = rowIndex * CustomContainerMenu.SLOTS_PER_ROW
        for (i in 0..<CustomContainerMenu.SLOTS_PER_ROW) {
            set(start + i, button)
        }
    }

    fun fillColumn(columnIndex: Int, button: ItemButton) {
        for (i in 0..<columns) {
            set(i * CustomContainerMenu.SLOTS_PER_ROW + columnIndex, button)
        }
    }

    internal fun toInitializer(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider): SimpleContainer.() -> Unit {
        return {
            for ((slot, buttonProvider) in buttons) {
                setItem(slot, buttonProvider.getButton().itemStack(mod, registryAccess))
            }
        }
    }
}
