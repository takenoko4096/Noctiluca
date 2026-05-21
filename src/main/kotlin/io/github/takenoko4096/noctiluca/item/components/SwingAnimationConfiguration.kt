package io.github.takenoko4096.noctiluca.item.components

import io.github.takenoko4096.noctiluca.NoctilucaDsl
import io.github.takenoko4096.noctiluca.NoctilucaModInitializer
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.SwingAnimationType
import net.minecraft.world.item.component.SwingAnimation

@NoctilucaDsl
class SwingAnimationConfiguration(mod: NoctilucaModInitializer, callback: SwingAnimationConfiguration.() -> Unit) : AbstractComponentConfiguration<SwingAnimation>(mod, DataComponents.SWING_ANIMATION) {
    private var animationType: SwingAnimationType = SwingAnimationType.WHACK

    var duration: Int = 6

    init {
        callback()
    }

    fun none() {
        animationType = SwingAnimationType.NONE
    }

    fun whack() {
        animationType = SwingAnimationType.WHACK
    }

    fun stab() {
        animationType = SwingAnimationType.STAB
    }

    override fun build(): SwingAnimation {
        return SwingAnimation(
            animationType,
            duration
        )
    }
}
