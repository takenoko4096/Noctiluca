package io.github.takenoko4096.noctiluca.render.model.block

import net.minecraft.world.level.block.state.properties.Property

class PropertyVariants1<T : Comparable<T>> internal constructor(val property: Property<T>) : PropertyVariants() {
    val selects: MutableSet<NonClientBlockSelect1<T>> = mutableSetOf()

    fun case(value1: T, variant: NonClientBlockModelVariant) {
        selects.add(
            NonClientBlockSelect1(value1, variant)
        )
    }
}
