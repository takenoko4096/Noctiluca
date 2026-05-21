package io.github.takenoko4096.noctiluca.mixin;

import io.github.takenoko4096.noctiluca.container.CustomContainerMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
@NullMarked
public abstract class ServerPlayerMixin {
    @Shadow public abstract void doCloseContainer();

    @Inject(method = "closeContainer()V", at = @At("HEAD"))
    public void injectCloseContainer(CallbackInfo info) {
        // CustomContainerMenu.onClose(containerMenu);
    }
}
