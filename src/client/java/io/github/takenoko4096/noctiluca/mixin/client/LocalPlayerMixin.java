package io.github.takenoko4096.noctiluca.mixin.client;

import com.mojang.authlib.GameProfile;
import io.github.takenoko4096.noctiluca.container.CustomContainerMenu;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
@NullMarked
public abstract class LocalPlayerMixin extends Player {
    public LocalPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "closeContainer()V", at = @At("HEAD"))
    public void injectCloseContainer(CallbackInfo info) {
        if (containerMenu instanceof CustomContainerMenu customContainerMenu) {
            CustomContainerMenu.Companion.invokeOnClose(customContainerMenu, this);
        }
    }
}
