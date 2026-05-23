package io.github.takenoko4096.noctiluca.item

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.item.components.*
import io.github.takenoko4096.noctiluca.text.SectionComponentBuilder
import io.github.takenoko4096.noctiluca.text.component
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.enchantment.Enchantments

@NoctilucaDsl
open class ItemComponents internal constructor(private val mod: NoctilucaModInitializer, private val dataSource: HolderLookup.Provider?, callback: ItemComponents.() -> Unit) {
    private val components = mutableListOf<AbstractItemComponent<*>>()

    val templates = ItemDefaultComponentSets()

    init {
        callback()
    }

    internal fun applyToProperties(target: Item.Properties) {
        components.forEach {
            it.set(target)
        }
    }

    internal fun createStack(target: ItemStackTemplate): ItemStack {
        val builder = DataComponentPatch.builder()

        components.forEach {
            it.set(builder)
        }

        return target.apply(builder.build())
    }

    fun <T : Any> negative(type: DataComponentType<T>) {
        components.add(ItemComponent.negative(type))
    }

    fun attributeModifiers(callback: AttributeModifiersConfiguration.() -> Unit) {
        components.add(
            AttributeModifiersConfiguration(
                mod,
                callback
            ).toComponent())
    }

    fun customName(builder: SectionComponentBuilder.() -> Unit) {
        customName(component(builder))
    }

    fun customName(component: Component) {
        components.add(ItemComponent.valued(DataComponents.CUSTOM_NAME, component))
    }

    fun damage(value: Int) {
        components.add(ItemComponent.valued(DataComponents.DAMAGE, value))
    }

    fun enchantable(callback: EnchantableConfiguration.() -> Unit) {
        components.add(EnchantableConfiguration(mod, callback).toComponent())
    }

    fun enchantmentGlintOverride(flag: Boolean) {
        components.add(ItemComponent.valued(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, flag))
    }

    fun enchantments(callback: EnchantmentsConfiguration.() -> Unit) {
        if (dataSource == null) {
            throw IllegalStateException()
        }

        components.add(EnchantmentsConfiguration(mod, dataSource, callback).toComponent())
    }

    fun equippable(callback: EquippableConfiguration.() -> Unit) {
        components.add(EquippableConfiguration(mod, callback).toComponent())
    }

    fun food(callback: FoodConfiguration.() -> Unit) {
        components.add(FoodConfiguration(mod, callback).toComponent())
    }

    fun glider() {
        components.add(ItemComponent.nonValued(DataComponents.GLIDER))
    }

    fun itemModel(identifier: Identifier) {
        components.add(ItemComponent.valued(DataComponents.ITEM_MODEL, identifier))
    }

    fun itemName(builder: SectionComponentBuilder.() -> Unit) {
        itemName(component(builder))
    }

    fun itemName(component: Component) {
        components.add(ItemComponent.valued(DataComponents.ITEM_NAME, component))
    }

    fun lore(callback: LoreConfiguration.() -> Unit) {
        components.add(LoreConfiguration(mod, callback).toComponent())
    }

    fun maxDamage(value: Int) {
        components.add(ItemComponent.valued(DataComponents.MAX_DAMAGE, value))
    }

    fun maxStackSize(value: Int) {
        components.add(ItemComponent.valued(DataComponents.MAX_STACK_SIZE, value))
    }

    fun minimumAttackCharge(requiredRate: Float) {
        components.add(ItemComponent.valued(DataComponents.MINIMUM_ATTACK_CHARGE, requiredRate))
    }

    fun rarity(callback: RarityConfiguration.() -> Unit) {
        components.add(RarityConfiguration(mod, callback).toComponent())
    }

    fun recipes(vararg recipes: ResourceKey<Recipe<*>>) {
        components.add(ItemComponent.valued(DataComponents.RECIPES, recipes.toList()))
    }

    fun repairCost(cost: Int) {
        components.add(ItemComponent.valued(DataComponents.REPAIR_COST, cost))
    }

    fun repairable(callback: RepairableConfiguration.() -> Unit) {
        components.add(RepairableConfiguration(mod, callback).toComponent())
    }

    fun swingAnimation(callback: SwingAnimationConfiguration.() -> Unit) {
        components.add(SwingAnimationConfiguration(mod, callback).toComponent())
    }

    fun tool(callback: ToolConfiguration.() -> Unit) {
        components.add(ToolConfiguration(mod, callback).toComponent())
    }

    fun tooltipDisplay(callback: TooltipDisplayConfiguration.() -> Unit) {
        components.add(TooltipDisplayConfiguration(mod, callback).toComponent())
    }

    fun unbreakable() {
        components.add(ItemComponent.nonValued(DataComponents.UNBREAKABLE))
    }

    fun weapon(callback: WeaponConfiguration.() -> Unit) {
        components.add(WeaponConfiguration(mod, callback).toComponent())
    }

    fun customData(callback: CustomDataConfiguration.() -> Unit) {
        components.add(CustomDataConfiguration(mod, callback).toComponent())
    }

    fun by(template: ItemDefaultComponentSet) {
        template.apply(this)
    }
}
