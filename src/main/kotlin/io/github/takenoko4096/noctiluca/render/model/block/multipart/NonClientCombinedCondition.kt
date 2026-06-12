package io.github.takenoko4096.noctiluca.render.model.block.multipart

sealed class NonClientCombinedCondition(val terms: List<AbstractNonClientCondition>) : AbstractNonClientCondition() {
    class And(terms: List<AbstractNonClientCondition>) : NonClientCombinedCondition(terms)

    class Or(terms: List<AbstractNonClientCondition>) : NonClientCombinedCondition(terms)

}
