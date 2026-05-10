package io.github.takenoko4096.starlight.client.ui.container

import io.github.takenoko4096.starlight.container.CustomContainerMenu
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Inventory

class CustomContainerScreen(private val menu: CustomContainerMenu, inventory: Inventory, title: Component) : AbstractContainerScreen<CustomContainerMenu>(menu, inventory, title, 176, 114 + menu.columnCount * CustomContainerMenu.SLOT_GRID_SIZE) {
    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractBackground(graphics, mouseX, mouseY, a)

        val xo = (width - imageWidth) / 2
        val yo = (height - imageHeight) / 2

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            BACKGROUND_TEXTURE,
            xo,
            yo,
            0.0f,
            0.0f,
            imageWidth,
            TEXTURE_OFFSET_MAGIC_NUMBER + menu.columnCount * CustomContainerMenu.SLOT_GRID_SIZE,
            256,
            256
        )

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            BACKGROUND_TEXTURE,
            xo,
            yo + TEXTURE_OFFSET_MAGIC_NUMBER + menu.columnCount * CustomContainerMenu.SLOT_GRID_SIZE,
            0.0f,
            126.0f,
            imageWidth,
            96,
            256,
            256
        )
    }

    companion object {
        val BACKGROUND_TEXTURE: Identifier = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png")

        const val TEXTURE_OFFSET_MAGIC_NUMBER = 17
    }
}
