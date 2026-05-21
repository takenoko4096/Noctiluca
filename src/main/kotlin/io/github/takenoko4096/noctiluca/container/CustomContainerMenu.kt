package io.github.takenoko4096.noctiluca.container

import io.github.takenoko4096.noctiluca.Noctiluca
import net.minecraft.core.NonNullList
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.ContainerListener
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class CustomContainerMenu internal constructor(
    id: Int,
    inventory: Inventory,
    val columnCount: Int,
    initializer: SimpleContainer.() -> Unit = {},
    private val onClick: ((Player, Int, Int, ContainerInput, NonNullList<Slot>) -> Boolean)? = null,
    private val onSlotChanged: ((Player, Int, ItemStack, NonNullList<Slot>) -> Unit)? = null,
    private val onClose: ((Player, NonNullList<Slot>) -> Unit)? = null
) : AbstractContainerMenu(getOrCreateType(columnCount),id) {
    private val container = SimpleContainer(SLOTS_PER_ROW * columnCount)

    init {
        checkContainerSize(container, SLOTS_PER_ROW * columnCount)
        container.startOpen(inventory.player)

        for (y in 0..<columnCount) {
            for (x in 0..<SLOTS_PER_ROW) {
                addSlot(Slot(
                    container,
                    y * SLOTS_PER_ROW + x,
                    X_PADDING + x * SLOT_GRID_SIZE,
                    Y_UPPER_PADDING + y * SLOT_GRID_SIZE
                ))
            }
        }

        for (y in 0..2) {
            for (x in 0..<SLOTS_PER_ROW) {
                addSlot(Slot(
                    inventory,
                    SLOTS_PER_ROW + y * SLOTS_PER_ROW + x,
                    X_PADDING + x * SLOT_GRID_SIZE,
                    Y_UPPER_PADDING + columnCount * SLOT_GRID_SIZE + Y_MIDDLE_PADDING + y * SLOT_GRID_SIZE
                ))
            }
        }

        for (x in 0..<SLOTS_PER_ROW) {
            addSlot(Slot(
                inventory,
                x,
                X_PADDING + x * SLOT_GRID_SIZE,
                Y_UPPER_PADDING + columnCount * SLOT_GRID_SIZE + PLAYER_INVENTORY_COLUMN * SLOT_GRID_SIZE + Y_MIDDLE_PADDING + Y_LOWER_PADDING
            ))
        }

        container.initializer()

        addSlotListener(object : ContainerListener {
            override fun slotChanged(menu: AbstractContainerMenu, slotIndex: Int, itemStack: ItemStack) {
                onSlotChanged?.invoke(inventory.player, slotIndex, itemStack, slots)
            }

            override fun dataChanged(menu: AbstractContainerMenu, id: Int, value: Int) {
                Noctiluca.logger.info("dataChanged: id=$id, value=$value")
            }
        })
    }

    override fun quickMoveStack(player: Player, slotIndex: Int): ItemStack {
        val slot = slots[slotIndex] ?: return ItemStack.EMPTY

        if (slot.hasItem()) {
            val stack = slot.item

            if (isContainerSlot(slotIndex)) {
                if (!moveItemStackTo(stack, columnCount * SLOTS_PER_ROW, slots.size, true)) {
                    return ItemStack.EMPTY
                }
            }
            else {
                if (!moveItemStackTo(stack, 0, columnCount * SLOTS_PER_ROW, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (stack.isEmpty) {
                slot.setByPlayer(ItemStack.EMPTY)
            }
            else {
                slot.setChanged()
            }

            return stack.copy()
        }
        else {
            return ItemStack.EMPTY
        }
    }

    override fun stillValid(player: Player) = container.stillValid(player)

    private fun isContainerSlot(slot: Int): Boolean = 0 <= slot && slot < columnCount * SLOTS_PER_ROW

    override fun clicked(slotIndex: Int, buttonNum: Int, containerInput: ContainerInput, player: Player) {
        if (onClick == null) {
            super.clicked(slotIndex, buttonNum, containerInput, player)
        }
        else when (containerInput) {
            ContainerInput.CLONE, ContainerInput.THROW, ContainerInput.PICKUP, ContainerInput.SWAP, ContainerInput.QUICK_CRAFT -> {
                // clone, throw, pickup: 非コンテナではコンテナ外のもののみ許可
                // swap(多分１とかFキー押してカーソル合わせたものと入れ替える操作のこと): 非コンテナではターゲットがコンテナ外のもののみ許可
                // quick craft(試した限りではspreadのこと): 非コンテナではコンテナ外のもののみ許可
                if (isContainerSlot(slotIndex)) {
                    val doOperation = onClick(player, slotIndex, buttonNum, containerInput, slots)
                    if (doOperation) {
                        super.clicked(slotIndex, buttonNum, containerInput, player)
                    }
                }
                else {
                    super.clicked(slotIndex, buttonNum, containerInput, player)
                }
            }
            ContainerInput.QUICK_MOVE -> {
                // quick move: 非コンテナでは常に禁止
                val doOperation = onClick(player, slotIndex, buttonNum, containerInput, slots)
                if (doOperation) {
                    super.clicked(slotIndex, buttonNum, containerInput, player)
                }
            }
            ContainerInput.PICKUP_ALL -> {
                // pickup all(多分これは左クリック2回押しで同じアイテムをまとめる操作のこと): 非コンテナでは常に禁止、コンテナでは非コンテナのアイテムを取らないような改造(?)コードを実行

                if (isContainerSlot(slotIndex)) {
                    val doOperation = onClick(player, slotIndex, buttonNum, containerInput, slots)
                    if (doOperation) {
                        super.clicked(slotIndex, buttonNum, containerInput, player)
                    }
                }
                else {
                    val slot = slots[slotIndex] as Slot
                    val carried = carried
                    if (!carried.isEmpty && (!slot.hasItem() || !slot.mayPickup(player))) {
                        val start = if (buttonNum == 0) 0 else slots.size - 1
                        val step = if (buttonNum == 0) 1 else -1

                        for (pass in 0..1) {
                            var i = start
                            while (i >= 0 && i < slots.size && carried.count < carried.maxStackSize) {
                                // inject
                                if (isContainerSlot(i)) {
                                    i += step
                                    continue
                                }

                                val target = slots[i] as Slot
                                if (target.hasItem() && canItemQuickReplace(target, carried, true) && target.mayPickup(player) && canTakeItemForPickAll(carried, target)) {
                                    val itemStack = target.item
                                    if (pass != 0 || itemStack.count != itemStack.maxStackSize) {
                                        val removed = target.safeTake(
                                            itemStack.count,
                                            carried.maxStackSize - carried.count,
                                            player
                                        )
                                        carried.grow(removed.count)
                                    }
                                }
                                i += step
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val X_PADDING = 8

        const val Y_UPPER_PADDING = 18 // prev=17

        const val Y_MIDDLE_PADDING = 13

        const val Y_LOWER_PADDING = 4

        const val SLOT_GRID_SIZE = 18

        const val SLOTS_PER_ROW = 9

        const val PLAYER_INVENTORY_COLUMN = 3

        private val types = mutableMapOf<Int, MenuType<CustomContainerMenu>>()

        private fun getOrCreateType(columnCount: Int): MenuType<CustomContainerMenu> {
            if (columnCount !in 1..6) {
                throw IllegalArgumentException("column count !in 1..6")
            }

            if (columnCount !in types) {
                val type = MenuType(
                    { id, inventory ->
                        CustomContainerMenu(id, inventory, columnCount)
                    },
                    FeatureFlagSet.of()
                )

                types[columnCount] = type
                Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(Noctiluca.identifier, "type_$columnCount"), type)
            }

            return types[columnCount]!!
        }

        fun invokeOnClose(menu: CustomContainerMenu, player: Player) {
            menu.onClose?.invoke(player, menu.slots)
        }

        val TYPE_1 = getOrCreateType(1)
        val TYPE_2 = getOrCreateType(2)
        val TYPE_3 = getOrCreateType(3)
        val TYPE_4 = getOrCreateType(4)
        val TYPE_5 = getOrCreateType(5)
        val TYPE_6 = getOrCreateType(6)
    }
}
