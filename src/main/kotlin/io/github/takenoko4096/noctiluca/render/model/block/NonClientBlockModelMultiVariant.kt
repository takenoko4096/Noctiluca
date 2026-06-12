package io.github.takenoko4096.noctiluca.render.model.block

class NonClientBlockModelMultiVariant(val variants: List<NonClientBlockModelVariant>, mutators: List<NonClientVariantMutator>) : AbstractBlockVariant(mutators) {
    fun mutatedCopy(mutator: NonClientVariantMutator): NonClientBlockModelMultiVariant {
        return NonClientBlockModelMultiVariant(
            variants,
            mutators + mutator
        )
    }

    companion object {
        fun flat(list: List<AbstractBlockVariant>): List<NonClientBlockModelVariant> {
            return list.flatMap {
                when (it) {
                    is NonClientBlockModelVariant -> listOf(it)
                    is NonClientBlockModelMultiVariant -> it.variants
                }
            }
        }
    }
}
