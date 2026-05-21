package io.github.takenoko4096.noctiluca.render.model.item.builder.condition

import net.minecraft.core.component.predicates.DataComponentPredicate

class ComponentCondition<C : DataComponentPredicate>(val type: DataComponentPredicate.Type<C>, val value: C) : Condition<C>()
