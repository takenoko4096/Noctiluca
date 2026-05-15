package io.github.takenoko4096.starlight.registry.block

import io.github.takenoko4096.starlight.StarlightDSL
import io.github.takenoko4096.starlight.item.ItemComponents
import net.minecraft.world.item.Item

@StarlightDSL
class BlockItemPropertiesConfiguration internal constructor(private val configuration: ModBlockConfiguration, callback: BlockItemPropertiesConfiguration.() -> Unit) {
    private var translationKey: String? = null

    private var components: ItemComponents? = null

    init {
        callback()
    }

    fun translationKeyOf(value: String) {
        translationKey = "block.${configuration.registry.mod.identifier}.$value"
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

        if (components != null) {
            components!!.applyToProperties(properties)
        }

        return properties
    }
}
