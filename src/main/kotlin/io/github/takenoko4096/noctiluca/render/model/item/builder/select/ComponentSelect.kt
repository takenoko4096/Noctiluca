package io.github.takenoko4096.noctiluca.render.model.item.builder.select

import net.minecraft.core.component.DataComponentType

class ComponentSelect<C : Any>(val componentType: DataComponentType<C>) : Select<C>()
