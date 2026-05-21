package io.github.takenoko4096.noctiluca.render.model.item.builder.select

import io.github.takenoko4096.noctiluca.render.model.item.builder.ItemModelBuilder

class SelectCase<C>(val `when`: Set<C>, val model: ItemModelBuilder)
