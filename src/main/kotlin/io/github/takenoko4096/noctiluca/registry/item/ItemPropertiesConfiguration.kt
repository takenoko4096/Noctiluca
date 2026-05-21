package io.github.takenoko4096.noctiluca.registry.item

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.item.ItemComponents
import net.minecraft.world.item.Item

@NoctilucaDsl
class ItemPropertiesConfiguration internal constructor(private val configuration: ModItemConfiguration, callback: ItemPropertiesConfiguration.() -> Unit) {
    private var translationKey: String? = null

    private var components: ItemComponents? = null

    init {
        callback()
    }

    fun translationKeyOf(value: String) {
        translationKey = "item.${configuration.registry.mod.identifier}.$value"
    }

    fun translationKeyAuto() {
        translationKeyOf(configuration.identifier)
    }

    fun components(callback: ItemComponents.() -> Unit) {
        components = ItemComponents(configuration.registry.mod, null, callback)
    }

    internal fun build(): Item.Properties {
        val properties = Item.Properties()
        properties.setId(configuration.itemResourceKey)

        if (translationKey == null) {
            translationKeyAuto()
        }

        properties.overrideDescription(translationKey!!)

        if (components == null) throw IllegalStateException("Cannot build item properties: item components is unset")

        components!!.applyToProperties(properties)

        return properties
    }
}
