package io.github.takenoko4096.noctiluca.execute

import io.github.takenoko4096.noctiluca.execute.arguments.selector.EntitySelector
import io.github.takenoko4096.noctiluca.execute.arguments.selector.SelectorArgument
import net.minecraft.world.entity.EntityType

fun execute() {
    EntitySelector.`@s` {
        x(0.0)
        y(0.1)
        z(-2.0)
        type(EntityType.ALLAY)
    }
}
