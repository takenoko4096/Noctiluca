package io.github.takenoko4096.noctiluca.item.components

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import net.minecraft.ChatFormatting
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.attributes.DefaultAttributes
import net.minecraft.world.item.component.ItemAttributeModifiers

@NoctilucaDsl
class AttributeModifiersConfiguration internal constructor(mod: NoctilucaModInitializer, callback: AttributeModifiersConfiguration.() -> Unit) : AbstractComponentConfiguration<ItemAttributeModifiers>(mod, DataComponents.ATTRIBUTE_MODIFIERS) {
    private val entries = mutableListOf<ItemAttributeModifiers.Entry>()

    private var id: Int = 0

    init {
        callback()
    }

    private fun modifier(attribute: Holder<Attribute>, callback: ModifierEntryConfiguration.() -> Unit) {
        entries.add(ModifierEntryConfiguration(attribute, this, callback).build())
    }

    fun armor(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ARMOR, callback)
    }

    fun armorToughness(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ARMOR_TOUGHNESS, callback)
    }

    fun attackDamage(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ATTACK_DAMAGE, callback)
    }

    fun attackKnockback(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ATTACK_KNOCKBACK, callback)
    }

    fun attackSpeed(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ATTACK_SPEED, callback)
    }

    fun blockBreakSpeed(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.BLOCK_BREAK_SPEED, callback)
    }

    fun blockInteractionRange(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.BLOCK_INTERACTION_RANGE, callback)
    }

    fun burningTime(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.BURNING_TIME, callback)
    }

    fun cameraDistance(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.CAMERA_DISTANCE, callback)
    }

    fun explosionKnockbackResistance(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, callback)
    }

    fun entityInteractionRange(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.ENTITY_INTERACTION_RANGE, callback)
    }

    fun fallDamageMultiplier(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.FALL_DAMAGE_MULTIPLIER, callback)
    }

    fun flyingSpeed(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.FLYING_SPEED, callback)
    }

    fun followRange(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.FOLLOW_RANGE, callback)
    }

    fun gravity(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.GRAVITY, callback)
    }

    fun jumpStrength(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.JUMP_STRENGTH, callback)
    }

    fun knockbackResistance(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.KNOCKBACK_RESISTANCE, callback)
    }

    fun luck(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.LUCK, callback)
    }

    fun maxAbsorption(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.MAX_ABSORPTION, callback)
    }

    fun maxHealth(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.MAX_HEALTH, callback)
    }

    fun miningEfficiency(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.MINING_EFFICIENCY, callback)
    }

    fun movementEfficiency(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.MOVEMENT_EFFICIENCY, callback)
    }

    fun movementSpeed(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.MOVEMENT_SPEED, callback)
    }

    fun oxygenBonus(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.OXYGEN_BONUS, callback)
    }

    fun safeFallDistance(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.SAFE_FALL_DISTANCE, callback)
    }

    fun scale(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.SCALE, callback)
    }

    fun sneakingSpeed(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.SNEAKING_SPEED, callback)
    }

    fun spawnReinforcementsChance(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.SPAWN_REINFORCEMENTS_CHANCE, callback)
    }

    fun stepHeight(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.STEP_HEIGHT, callback)
    }

    fun submergedMiningSpeed(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.SUBMERGED_MINING_SPEED, callback)
    }

    fun sweepingDamageRatio(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.SWEEPING_DAMAGE_RATIO, callback)
    }

    fun temptRange(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.TEMPT_RANGE, callback)
    }

    fun waterMovementEfficiency(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.WATER_MOVEMENT_EFFICIENCY, callback)
    }

    fun waypointTransmitRange(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.WAYPOINT_TRANSMIT_RANGE, callback)
    }

    fun waypointReceiveRange(callback: ModifierEntryConfiguration.() -> Unit) {
        modifier(Attributes.WAYPOINT_RECEIVE_RANGE, callback)
    }

    override fun build(): ItemAttributeModifiers {
        return ItemAttributeModifiers(entries.toList())
    }

    @NoctilucaDsl
    class ModifierEntryConfiguration(private val attribute: Holder<Attribute>, private val parent: AttributeModifiersConfiguration, callback: ModifierEntryConfiguration.() -> Unit) {
        private var identifier: Identifier = parent.mod.identifierOf((parent.id++).toString())

        private var operation: AttributeModifier.Operation = AttributeModifier.Operation.ADD_VALUE

        private var group = EquipmentSlotGroup.ANY

        private var display: ItemAttributeModifiers.Display = ItemAttributeModifiers.Display.attributeModifiers()

        var id: String
            get() = identifier.path
            set(value) {
                identifier = parent.mod.identifierOf(value)
            }

        var value: Double = attribute.value().defaultValue

        init {
            callback()
        }

        fun operation(callback: OperationConfiguration.() -> Unit) {
            operation = OperationConfiguration(callback).operation
        }

        fun slot(callback: EquipmentSlotGroupConfiguration.() -> Unit) {
            group = EquipmentSlotGroupConfiguration(callback).group
        }

        fun display(callback: DisplayConfiguration.() -> Unit) {
            display = DisplayConfiguration(attribute.value(), operation, value, callback).display
        }

        internal fun build(): ItemAttributeModifiers.Entry {
            return ItemAttributeModifiers.Entry(
                attribute,
                AttributeModifier(
                    identifier,
                    value,
                    operation
                ),
                group,
                display
            )
        }

        @NoctilucaDsl
        class OperationConfiguration internal constructor(callback: OperationConfiguration.() -> Unit) {
            internal var operation = AttributeModifier.Operation.ADD_VALUE

            init {
                callback()
            }

            fun addValue() {
                operation = AttributeModifier.Operation.ADD_VALUE
            }

            fun addMultipliedBase() {
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            }

            fun addMultipliedTotal() {
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            }
        }

        @NoctilucaDsl
        class EquipmentSlotGroupConfiguration internal constructor(callback: EquipmentSlotGroupConfiguration.() -> Unit) {
            internal var group = EquipmentSlotGroup.ANY

            val armor = Armor(this)

            val weapon = Weapon(this)

            init {
                callback()
            }

            fun any() {
                group = EquipmentSlotGroup.ANY
            }

            fun saddle() {
                group = EquipmentSlotGroup.SADDLE
            }

            class Armor internal constructor(private val parent: EquipmentSlotGroupConfiguration) {
                fun any() {
                    parent.group = EquipmentSlotGroup.ARMOR
                }

                fun head() {
                    parent.group = EquipmentSlotGroup.HEAD
                }

                fun chest() {
                    parent.group = EquipmentSlotGroup.CHEST
                }

                fun legs() {
                    parent.group = EquipmentSlotGroup.LEGS
                }

                fun feet() {
                    parent.group = EquipmentSlotGroup.FEET
                }

                fun body() {
                    parent.group = EquipmentSlotGroup.BODY
                }
            }

            class Weapon internal constructor(private val parent: EquipmentSlotGroupConfiguration) {
                fun any() {
                    parent.group = EquipmentSlotGroup.HAND
                }

                fun mainhand() {
                    parent.group = EquipmentSlotGroup.MAINHAND
                }

                fun offhand() {
                    parent.group = EquipmentSlotGroup.OFFHAND
                }
            }
        }

        @NoctilucaDsl
        class DisplayConfiguration internal constructor(
            private val attribute: Attribute,
            private val operation: AttributeModifier.Operation,
            private val value: Double,
            callback: DisplayConfiguration.() -> Unit
        ) {
            internal var display: ItemAttributeModifiers.Display = ItemAttributeModifiers.Display.attributeModifiers()

            init {
                callback()
            }

            fun builtin() {
                val defaults = DefaultAttributes.getSupplier(EntityType.PLAYER)
                var output = value

                if (attribute == Attributes.ATTACK_DAMAGE.value()) {
                    output += defaults.getBaseValue(Attributes.ATTACK_DAMAGE)
                }
                else if (attribute == Attributes.ATTACK_SPEED.value()) {
                    output += defaults.getBaseValue(Attributes.ATTACK_SPEED)
                }

                if (operation == AttributeModifier.Operation.ADD_MULTIPLIED_BASE || operation == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    output *= 100.0
                }
                else if (attribute == Attributes.KNOCKBACK_RESISTANCE.value()) {
                    output *= 10.0
                }

                override(
                    CommonComponents.space()
                        .append(
                            Component.translatable(
                                "attribute.modifier.equals.${operation.id()}",
                                Component.literal(ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(output)),
                                Component.translatable(attribute.descriptionId)
                            )
                        )
                        .withStyle(ChatFormatting.DARK_GREEN)
                )
            }

            fun override(component: Component) {
                display = ItemAttributeModifiers.Display.override(component)
            }

            fun hidden() {
                display = ItemAttributeModifiers.Display.hidden()
            }
        }
    }
}
