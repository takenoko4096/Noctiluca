package io.github.takenoko4096.noctiluca.render.model.block

import io.github.takenoko4096.noctiluca.render.model.NonClientModel

class NonClientBlockModelVariant internal constructor(val model: NonClientModel, mutators: List<NonClientVariantMutator>) : AbstractBlockVariant(mutators)