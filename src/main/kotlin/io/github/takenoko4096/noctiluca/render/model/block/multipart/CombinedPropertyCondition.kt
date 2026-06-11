package io.github.takenoko4096.noctiluca.render.model.block.multipart

sealed class CombinedPropertyCondition(val terms: List<ConditionTerm<*>>) {
    class And(terms: List<ConditionTerm<*>>) : CombinedPropertyCondition(terms)

    class Or(terms: List<ConditionTerm<*>>) : CombinedPropertyCondition(terms)
}
