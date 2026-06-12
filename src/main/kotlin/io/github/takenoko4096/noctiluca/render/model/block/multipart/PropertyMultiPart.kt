package io.github.takenoko4096.noctiluca.render.model.block.multipart

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.render.model.block.NonClientBlockModelVariant

data class PropertyMultiPart(val `when`: NonClientCombinedCondition?, val apply: List<NonClientBlockModelVariant>) {
    @NoctilucaDsl
    class Builder internal constructor(callback: Builder.() -> Unit) {
        private var `when`: NonClientCombinedCondition? = null

        private var variants: List<NonClientBlockModelVariant> = listOf()

        init {
            callback()
        }

        fun `when`(callback: NonClientCombinedCondition.Provider.() -> NonClientCombinedCondition) {
            val provider = NonClientCombinedCondition.Provider()
            `when` = provider.callback()
        }

        fun apply(vararg variants: NonClientBlockModelVariant) {
            this.variants = variants.toList()
        }

        internal fun build(): PropertyMultiPart {
            return PropertyMultiPart(
                `when`,
                variants
            )
        }
    }
}
