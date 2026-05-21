package io.github.takenoko4096.noctiluca.datagen.model.custom

import io.github.takenoko4096.noctiluca.render.model.custom.NonClientCustomModel
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block

class ClientCustomBlockModel(block: Block, model: NonClientCustomModel, generators: BlockModelGenerators) : ClientCustomModel(model) {
    override val identifier: Identifier = template.createWithSuffix(
        block,
        if (model.options.suffix == null) "" else '_' + model.options.suffix!!,
        mapping,
        generators.modelOutput
    )
}
