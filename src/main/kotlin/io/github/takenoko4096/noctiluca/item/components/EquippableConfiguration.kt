package io.github.takenoko4096.noctiluca.item.components

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.render.TexturePath
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.equipment.EquipmentAsset
import net.minecraft.world.item.equipment.Equippable
import java.util.Optional

@NoctilucaDsl
class EquippableConfiguration(mod: NoctilucaModInitializer, callback: EquippableConfiguration.() -> Unit) : AbstractComponentConfiguration<Equippable>(mod, DataComponents.EQUIPPABLE) {
    private var sounds = SoundEventConfiguration {}

    private var slot: EquipmentSlot? = null

    private var asset: ResourceKey<EquipmentAsset>? = null

    private var cameraOverlay: Identifier? = null

    private var entityTypeSet: HolderSet<EntityType<*>>? = null

    private var flags: FlagsConfiguration = FlagsConfiguration {}

    init {
        callback()
    }

    fun sounds(callback: SoundEventConfiguration.() -> Unit) {
        sounds = SoundEventConfiguration(callback)
    }

    fun slot(callback: EquipmentSlotConfiguration.() -> Unit) {
        slot = EquipmentSlotConfiguration(callback).slot
    }

    fun model(asset: ResourceKey<EquipmentAsset>) {
        this.asset = asset
    }

    fun cameraOverlay(texture: TexturePath) {
        cameraOverlay = texture.identifier
    }

    fun entityTypes(tag: TagKey<EntityType<*>>) {
        entityTypeSet = BuiltInRegistries.ENTITY_TYPE.getOrThrow(tag)
    }

    fun flags(callback: FlagsConfiguration.() -> Unit) {
        flags = FlagsConfiguration(callback)
    }

    override fun build(): Equippable {
        if (slot == null) {
            throw IllegalStateException("'slot' is unset")
        }
        else if (sounds.equip == null) {
            throw IllegalStateException("'sounds.equip' is unset")
        }
        else if (sounds.shear == null) {
            throw IllegalStateException("'sounds.shear' is unset")
        }

        return Equippable(
            slot!!,
            sounds.equip!!,
            if (asset == null) Optional.empty() else Optional.of(asset!!),
            if (cameraOverlay == null) Optional.empty() else Optional.of(cameraOverlay!!),
            if (entityTypeSet == null) Optional.empty() else Optional.of(entityTypeSet!!),
            flags.dispensable,
            flags.swappable,
            flags.damageOnHurt,
            flags.equipOnInteract,
            flags.canBeSheared,
            sounds.shear!!
        )
    }

    @NoctilucaDsl
    class EquipmentSlotConfiguration internal constructor(callback: EquipmentSlotConfiguration.() -> Unit) {
        internal var slot: EquipmentSlot? = null

        val armor = Armor(this)

        val weapon = Weapon(this)

        init {
            callback()
        }

        fun saddle() {
            slot = EquipmentSlot.SADDLE
        }

        class Armor internal constructor(private val parent: EquipmentSlotConfiguration) {
            fun head() {
                parent.slot = EquipmentSlot.HEAD
            }

            fun chest() {
                parent.slot = EquipmentSlot.CHEST
            }

            fun legs() {
                parent.slot = EquipmentSlot.LEGS
            }

            fun feet() {
                parent.slot = EquipmentSlot.FEET
            }

            fun body() {
                parent.slot = EquipmentSlot.BODY
            }
        }

        class Weapon internal constructor(private val parent: EquipmentSlotConfiguration) {
            fun mainhand() {
                parent.slot = EquipmentSlot.MAINHAND
            }

            fun offhand() {
                parent.slot = EquipmentSlot.OFFHAND
            }
        }
    }

    @NoctilucaDsl
    class SoundEventConfiguration internal constructor(callback: SoundEventConfiguration.() -> Unit) {
        internal var equip: Holder<SoundEvent>? = null

        internal var shear: Holder<SoundEvent>? = null

        init {
            callback()
        }

        fun equip(sound: ResourceKey<SoundEvent>) {
            equip = BuiltInRegistries.SOUND_EVENT.getOrThrow(sound)
        }

        fun shear(sound: ResourceKey<SoundEvent>) {
            shear = BuiltInRegistries.SOUND_EVENT.getOrThrow(sound)
        }
    }

    @NoctilucaDsl
    class FlagsConfiguration internal constructor(callback: FlagsConfiguration.() -> Unit) {
        var dispensable: Boolean = true

        var swappable: Boolean = true

        var damageOnHurt: Boolean = true

        var equipOnInteract: Boolean = false

        var canBeSheared: Boolean = false

        init {
            callback()
        }
    }
}
