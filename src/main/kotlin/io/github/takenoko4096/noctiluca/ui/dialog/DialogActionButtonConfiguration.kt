package io.github.takenoko4096.noctiluca.ui.dialog

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import io.github.takenoko4096.noctiluca.text.SectionComponentBuilder
import io.github.takenoko4096.noctiluca.text.component
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.dialog.ActionButton
import net.minecraft.server.dialog.CommonButtonData
import net.minecraft.server.dialog.action.CustomAll
import net.minecraft.world.entity.player.Player
import java.util.Optional

@NoctilucaDsl
class DialogActionButtonConfiguration internal constructor(callback: DialogActionButtonConfiguration.() -> Unit) {
    private var label: Component = Component.empty()

    private var tooltip: Component? = null

    private var onClick: DialogCustomActionButtonClickEvent.() -> Unit = {}

    var width: Int = 150

    init {
        callback()
    }

    fun label(callback: SectionComponentBuilder.() -> Unit) {
        label = component(callback)
    }

    fun tooltip(callback: SectionComponentBuilder.() -> Unit) {
        tooltip = component(callback)
    }

    fun onClick(callback: DialogCustomActionButtonClickEvent.() -> Unit) {
        this.onClick = callback
    }

    private fun buildCommonButtonData(): CommonButtonData {
        return CommonButtonData(
            label,
            tooltip?.let { Optional.of(it) } ?: Optional.empty(),
            width
        )
    }

    internal fun build(mod: NoctilucaModInitializer, map: MutableMap<Identifier, DialogCustomActionButtonClickEvent.() -> Unit>): ActionButton {
        val identifier = mod.identifierOf("custom_dialog_action_${maximumId++}")

        map[identifier] = onClick

        return ActionButton(
            buildCommonButtonData(),
            Optional.of(
                CustomAll(
                    identifier,
                    Optional.empty()
                )
            )
        )
    }

    class DialogCustomActionButtonClickEvent internal constructor(
        val player: Player,
        val identifier: Identifier,
        val payload: Tag?
    )

    companion object {
        private var maximumId: UInt = 0u
    }
}
