package io.github.takenoko4096.noctiluca.render.model.item.builder.select

import net.minecraft.world.level.block.state.properties.Property

class BlockStateSelect<C : Comparable<C>>(val property: Property<C>) : Select<C>()