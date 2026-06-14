package io.github.takenoko4096.noctiluca.registry.item.templates

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.registry.item.CustomItem
import io.github.takenoko4096.noctiluca.registry.item.ModItemConfiguration
import io.github.takenoko4096.noctiluca.registry.translation.ModTranslationConfiguration
import net.minecraft.resources.Identifier

@NoctilucaDsl
abstract class ModItemTemplate<T : CustomItem> {
    protected var model: (ModItemConfiguration.ItemModelConfiguration.() -> Unit)? = null

    protected var translation: ModTranslationConfiguration.() -> Unit = {}

    fun model(callback: ModItemConfiguration.ItemModelConfiguration.() -> Unit) {
        model = callback
    }

    fun translation(callback: ModTranslationConfiguration.() -> Unit) {
        translation = callback
    }

    abstract fun getConfigurator(identifier: Identifier): ModItemConfiguration.() -> Unit
}
