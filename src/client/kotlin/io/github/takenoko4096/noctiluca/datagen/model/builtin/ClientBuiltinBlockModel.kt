package io.github.takenoko4096.noctiluca.datagen.model.builtin

import io.github.takenoko4096.noctiluca.render.model.builtin.NonClientBuiltinModel
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block

class ClientBuiltinBlockModel(block: Block, model: NonClientBuiltinModel, generators: BlockModelGenerators) : ClientBuiltinModel(model) {
    override val identifier: Identifier = template.createWithSuffix(
        block,
        if (model.options.suffix == null) "" else '_' + model.options.suffix!!,
        mapping,
        generators.modelOutput
    )
}
