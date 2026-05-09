package io.github.takenoko4096.starlight.client.ui.container

import io.github.takenoko4096.starlight.ui.container.CustomContainerMenu
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class CustomContainerScreen(menu: CustomContainerMenu, inventory: Inventory, title: Component) : AbstractContainerScreen<CustomContainerMenu>(menu, inventory, title) {
    override fun extractBackground(graphics: GuiGraphicsExtractor?, mouseX: Int, mouseY: Int, a: Float) {

    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor?, mouseX: Int, mouseY: Int, a: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)
    }

    override
}
