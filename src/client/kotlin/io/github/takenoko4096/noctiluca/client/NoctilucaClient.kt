package io.github.takenoko4096.noctiluca.client

import io.github.takenoko4096.noctiluca.Noctiluca
import io.github.takenoko4096.noctiluca.client.ui.container.CustomContainerScreen
import io.github.takenoko4096.noctiluca.container.CustomContainerMenu
import io.github.takenoko4096.noctiluca.datagen.NoctilucaDataGenerator
import net.minecraft.client.gui.screens.MenuScreens

object NoctilucaClient : NoctilucaClientModInitializer(Noctiluca) {
    override fun onInitialize() {
        MenuScreens.register(CustomContainerMenu.TYPE_1, ::CustomContainerScreen)
        MenuScreens.register(CustomContainerMenu.TYPE_2, ::CustomContainerScreen)
        MenuScreens.register(CustomContainerMenu.TYPE_3, ::CustomContainerScreen)
        MenuScreens.register(CustomContainerMenu.TYPE_4, ::CustomContainerScreen)
        MenuScreens.register(CustomContainerMenu.TYPE_5, ::CustomContainerScreen)
        MenuScreens.register(CustomContainerMenu.TYPE_6, ::CustomContainerScreen)
    }
}
