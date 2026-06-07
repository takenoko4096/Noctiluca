package io.github.takenoko4096.noctiluca.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Inject(method = "handlePortalTransitionEffect", at = @At("HEAD"))
    public void injectHandlePortalTransitionEffect(boolean active, CallbackInfo info) {

    }
}
