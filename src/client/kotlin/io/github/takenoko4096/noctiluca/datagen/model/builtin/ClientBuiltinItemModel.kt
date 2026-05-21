package io.github.takenoko4096.noctiluca.datagen.model.builtin

import io.github.takenoko4096.noctiluca.render.model.builtin.NonClientBuiltinModel
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item

class ClientBuiltinItemModel(item: Item, model: NonClientBuiltinModel, generators: ItemModelGenerators): ClientBuiltinModel(model) {
    init {
        if (model.options.suffix != null) {
            throw IllegalStateException("モデルオプション 'suffix' を指定することはできません: アイテムモデルは一つのアイテムの種類に対して常に単一であり、バリアントの概念はありません;　別のアイテムとして作成するか、 minecraft:item_modelコンポーネントの使用を検討してください")
        }
    }

    override val identifier: Identifier = template.create(item, mapping, generators.modelOutput)
}
