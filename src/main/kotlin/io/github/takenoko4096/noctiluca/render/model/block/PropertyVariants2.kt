package io.github.takenoko4096.noctiluca.render.model.block

import net.minecraft.world.level.block.state.properties.Property

class PropertyVariants2<T : Comparable<T>, U : Comparable<U>>(
    val property1: Property<T>,
    val property2: Property<U>
) : PropertyVariants() {
    val selects = mutableSetOf<NonClientBlockSelect2<T, U>>()

    fun case(value1: T, value2: U, variant: NonClientBlockModelVariant) {
        selects.add(
            NonClientBlockSelect2(value1, value2, variant)
        )
    }
}
