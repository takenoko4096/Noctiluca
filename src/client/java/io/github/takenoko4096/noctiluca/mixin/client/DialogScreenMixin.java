package io.github.takenoko4096.noctiluca.mixin.client;

import io.github.takenoko4096.noctiluca.Noctiluca;
import io.github.takenoko4096.noctiluca.network.ServerboundDialogClosePayload;
import io.github.takenoko4096.noctiluca.network.ServerboundDialogEscapePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.client.gui.screens.dialog.DialogScreen;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.DialogAction;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(DialogScreen.class)
public abstract class DialogScreenMixin extends Screen {
    @Shadow @Final private @Nullable Screen previousScreen;

    @Shadow private Supplier<Optional<ClickEvent>> onClose;

    @Shadow @Final private DialogConnectionAccess connectionAccess;

    protected DialogScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "onClose()V", at = @At("HEAD"), cancellable = true)
    public void injectOnClose(CallbackInfo info) {
        final var onCloseEventOptional = onClose.get();

        if (onCloseEventOptional.isPresent()) {
            final var onCloseEvent = onCloseEventOptional.get();
            if (onCloseEvent instanceof ClickEvent.Custom(Identifier id, Optional<Tag> payload) && id.getNamespace().equals(Noctiluca.INSTANCE.getIdentifier())) {
                info.cancel();
                minecraft.setScreen(previousScreen);
            }
        }

        ClientPlayNetworking.send(ServerboundDialogEscapePayload.INSTANCE);
    }

    @Inject(method = "runAction(Ljava/util/Optional;Lnet/minecraft/server/dialog/DialogAction;)V", at = @At("TAIL"))
    public void injectRunAction(Optional<ClickEvent> closeAction, DialogAction afterAction, CallbackInfo info) {
        if (closeAction.isPresent()) {
            final var event = closeAction.get();
            if (event instanceof ClickEvent.Custom(Identifier id, Optional<Tag> payload) && id.getNamespace().equals(Noctiluca.INSTANCE.getIdentifier()) && afterAction == DialogAction.CLOSE) {
                ClientPlayNetworking.send(ServerboundDialogClosePayload.INSTANCE);
            }
        }
    }
}
