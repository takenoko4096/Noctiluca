package io.github.takenoko4096.noctiluca.text

import net.minecraft.network.chat.Component

fun component(callback: SectionComponentBuilder.() -> Unit): Component {
    return SectionComponentBuilder(null, callback).build()
}
