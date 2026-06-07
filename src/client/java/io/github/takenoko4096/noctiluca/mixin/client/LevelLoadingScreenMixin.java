package io.github.takenoko4096.noctiluca.mixin.client;

import io.github.takenoko4096.noctiluca.registry.block.CustomPortalBlock;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.PortalProcessor;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin extends Screen {
    protected LevelLoadingScreenMixin(Component title) {
        super(title);
    }

    @Unique
    private @Nullable CustomPortalBlock getCustomPortalBlock() {
        final LocalPlayer player = minecraft.player;
        if (player == null) return null;
        final PortalProcessor portalProcessor = player.portalProcess;
        if (portalProcessor == null) return null;

        if (!(portalProcessor.portal instanceof CustomPortalBlock customPortalBlock)) {
            return null;
        }

        return customPortalBlock;
    }

    @Inject(method = "getNetherPortalSprite", at = @At("RETURN"), cancellable = true)
    public void setCustomPortalSprite(CallbackInfoReturnable<TextureAtlasSprite> info) {
        final CustomPortalBlock customPortalBlock = getCustomPortalBlock();
        if (customPortalBlock == null) return;

        final TextureAtlasSprite sprite = minecraft.getModelManager()
            .getBlockStateModelSet()
            .getParticleMaterial(customPortalBlock.defaultBlockState())
            .sprite();

        info.setReturnValue(sprite);
    }
}
