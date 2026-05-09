package io.github.takenoko4096.starlight.ui.container

import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class CustomContainerMenu(id: Int, inventory: Inventory, private val row: Int, private val column: Int) : AbstractContainerMenu(
    MenuType(
        { id, inventory ->
            CustomContainerMenu(id, inventory, row, column)
        },
        FeatureFlagSet.of()
    ),
    id
) {
    private val container = SimpleContainer(row * column)

    init {
        checkContainerSize(container, row * column)
        container.startOpen(inventory.player)

        for (y in 0..<column) {
            for (x in 0..<row) {
                addSlot(Slot(
                    container,
                    y * row + x,
                    X_PADDING + (MAX_SLOTS - row) * SLOT_GRID_SIZE + x * SLOT_GRID_SIZE,
                    Y_UPPER_PADDING + y * SLOT_GRID_SIZE
                ))
            }
        }

        for (y in 0..2) {
            for (x in 0..8) {
                addSlot(Slot(
                    inventory,
                    9 + y * row + x,
                    X_PADDING + (MAX_SLOTS - row) * SLOT_GRID_SIZE + x * SLOT_GRID_SIZE,
                    Y_UPPER_PADDING + column * SLOT_GRID_SIZE + Y_MIDDLE_PADDING + y * SLOT_GRID_SIZE
                ))
            }
        }

        for (x in 0..8) {
            addSlot(Slot(
                inventory,
                x,
                X_PADDING + (MAX_SLOTS - row) * SLOT_GRID_SIZE + x * SLOT_GRID_SIZE,
                Y_UPPER_PADDING + column * SLOT_GRID_SIZE + PLAYER_INVENTORY_COLUMN * SLOT_GRID_SIZE + Y_MIDDLE_PADDING + Y_LOWER_PADDING
            ))
        }
    }

    override fun quickMoveStack(player: Player, slotIndex: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun stillValid(player: Player) = container.stillValid(player)

    companion object {
        private const val X_PADDING = 8

        private const val Y_UPPER_PADDING = 17

        private const val Y_MIDDLE_PADDING = 13

        private const val Y_LOWER_PADDING = 4

        private const val SLOT_GRID_SIZE = 18

        private const val MAX_SLOTS = 9

        private const val PLAYER_INVENTORY_COLUMN = 3
    }
}
