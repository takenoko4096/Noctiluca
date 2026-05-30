package io.github.takenoko4096.noctiluca.execute.arguments.selector

import io.github.takenoko4096.noctiluca.execute.NoctilucaCommandSourceStack
import net.minecraft.world.entity.Entity

class EntitySelector(val type: SelectorType, val arguments: SelectorArguments) {
    fun getEntities(stack: NoctilucaCommandSourceStack): List<Entity> {
        return type.defaultArguments.patched(arguments).apply(stack, type.getCandidates(stack))
    }

    companion object {
        private fun create(type: SelectorType, builder: SelectorArguments.Builder.() -> Unit): EntitySelector {
            return EntitySelector(type, SelectorArguments.Builder(builder).build())
        }

        fun `@e`(builder: SelectorArguments.Builder.() -> Unit) = create(SelectorType.E, builder)

        fun `@n`(builder: SelectorArguments.Builder.() -> Unit) = create(SelectorType.N, builder)

        fun `@p`(builder: SelectorArguments.Builder.() -> Unit) = create(SelectorType.P, builder)

        fun `@r`(builder: SelectorArguments.Builder.() -> Unit) = create(SelectorType.R, builder)

        fun `@a`(builder: SelectorArguments.Builder.() -> Unit) = create(SelectorType.A, builder)

        fun `@s`(builder: SelectorArguments.Builder.() -> Unit) = create(SelectorType.S, builder)
    }
}
