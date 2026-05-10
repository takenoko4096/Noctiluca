package io.github.takenoko4096.starlight.ui.container

import io.github.takenoko4096.starlight.NoctilucaModInitializer
import io.github.takenoko4096.starlight.container.CustomContainerMenu
import io.github.takenoko4096.starlight.text.SectionComponentBuilder
import io.github.takenoko4096.starlight.text.component
import net.minecraft.core.HolderLookup
import net.minecraft.network.chat.Component
import net.minecraft.world.SimpleContainer

class ContainerInteractionConfiguration(callback: ContainerInteractionConfiguration.() -> Unit) {
    private var title: Component = Component.empty()

    private var contents: Contents = Contents(1) {}

    init {
        callback()
    }

    fun title(builder: SectionComponentBuilder.() -> Unit) {
        title = component(builder)
    }

    fun contents(columns: Int, callback: Contents.() -> Unit) {
        contents = Contents(columns, callback)
    }

    internal fun build(): ContainerInteraction {
        return ContainerInteraction(title, contents)
    }

    class Contents(internal var columns: Int, callback: Contents.() -> Unit) {
        internal val buttons = mutableMapOf<Int, ItemButton>()

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

        internal fun toContainerInitializer(mod: NoctilucaModInitializer, registryAccess: HolderLookup.Provider): SimpleContainer.() -> Unit {
            return {
                for ((slot, button) in buttons) {
                    setItem(slot, button.itemStack(mod, registryAccess))
                }
            }
        }
    }
}
