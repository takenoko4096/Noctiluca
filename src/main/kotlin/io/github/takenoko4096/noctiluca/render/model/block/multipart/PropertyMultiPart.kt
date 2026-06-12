package io.github.takenoko4096.noctiluca.render.model.block.multipart

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.render.model.block.AbstractBlockVariant
import io.github.takenoko4096.noctiluca.render.model.block.NonClientBlockModelMultiVariant
import io.github.takenoko4096.noctiluca.render.model.block.NonClientBlockModelVariant

data class PropertyMultiPart(val `when`: AbstractNonClientCondition?, val apply: List<NonClientBlockModelVariant>) {
    @NoctilucaDsl
    class Builder internal constructor(callback: Builder.() -> Unit) {
        private var `when`: AbstractNonClientCondition? = null

        private var variants: List<NonClientBlockModelVariant> = listOf()

        init {
            callback()
        }

        fun `when`(callback: MultiPartConditionProvider.() -> AbstractNonClientCondition) {
            val provider = MultiPartConditionProvider()
            `when` = provider.callback()
        }

        fun apply(vararg variants: AbstractBlockVariant) {
            this.variants = NonClientBlockModelMultiVariant.flat(variants.toList())
        }

        internal fun build(): PropertyMultiPart {
            return PropertyMultiPart(
                `when`,
                variants
            )
        }
    }
}
