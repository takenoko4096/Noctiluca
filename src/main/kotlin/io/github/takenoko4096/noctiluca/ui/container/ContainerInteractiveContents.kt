package io.github.takenoko4096.noctiluca.ui.container

import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.container.CustomContainerMenu
import net.minecraft.core.HolderLookup
import net.minecraft.world.SimpleContainer
import kotlin.collections.iterator

@NoctilucaDsl
class ContainerInteractiveContents(val columnCount: Int, callback: ContainerInteractiveContents.() -> Unit) {
    internal val buttons = mutableMapOf<Int, ItemButtonProvider>()

    init {
        callback()
    }

    fun indexAt(horizontalIndex: Int, verticalIndex: Int): Int {
        return horizontalIndex + CustomContainerMenu.SLOTS_PER_ROW * verticalIndex
    }

    fun lastRowIndex(): Int = columnCount - 1

    fun lastColumnIndex(): Int = CustomContainerMenu.SLOTS_PER_ROW - 1

    fun horizontalCenterIndex(): Int = (CustomContainerMenu.SLOTS_PER_ROW - 1) / 2

    fun put(slot: Int, button: ItemButton) {
        if (slot !in 0..<(CustomContainerMenu.SLOTS_PER_ROW * columnCount)) {
            throw IllegalArgumentException("cannot set button to slot $slot: slot index is out of bounds")
        }

        buttons[slot] = button
    }

    fun append(button: ItemButton) {
        for (i in 0..<(CustomContainerMenu.SLOTS_PER_ROW * columnCount)) {
            if (i !in buttons) {
                put(i, button)
                return
            }
        }

        throw IllegalArgumentException("cannot add button: container is full")
    }

    fun fillHorizontally(rowIndex: Int, button: ItemButton) {
        val start = rowIndex * CustomContainerMenu.SLOTS_PER_ROW
        for (i in 0..<CustomContainerMenu.SLOTS_PER_ROW) {
            put(start + i, button)
        }
    }

    fun fillVertically(columnIndex: Int, button: ItemButton) {
        for (i in 0..<columnCount) {
            put(i * CustomContainerMenu.SLOTS_PER_ROW + columnIndex, button)
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
