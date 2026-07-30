package io.github.takenoko4096.noctiluca.mixin.client;

import io.github.takenoko4096.noctiluca.registry.block.CustomPortalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.PortalProcessor;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Hud.class)
public abstract class HudMixin {
    @Shadow @Final private Minecraft minecraft;

    @Unique
    private @Nullable CustomPortalBlock getCustomPortalBlock() {
        final LocalPlayer player = minecraft.player;
        if (player == null) return null;
        final PortalProcessor portalProcessor = player.portalProcess;
        if (portalProcessor == null) {
            final Block block = player.level().getBlockState(player.blockPosition()).getBlock();

            if (block instanceof CustomPortalBlock customPortalBlock) {
                return customPortalBlock;
            }

            return null;
        }

        if (!(portalProcessor.portal instanceof CustomPortalBlock customPortalBlock)) {
            return null;
        }

        return customPortalBlock;
    }

    @ModifyVariable(method = "extractPortalOverlay", at = @At("STORE"), name = "color")
    public int setPortalOverlayColor(int color, GuiGraphicsExtractor graphics, float alpha) {
        final CustomPortalBlock customPortalBlock = getCustomPortalBlock();
        if (customPortalBlock == null) return ARGB.white(alpha);

        return customPortalBlock.getColor().getRgb().withAlpha(Math.round(alpha * 255)).getArgbValue();
    }

    @ModifyVariable(method = "extractPortalOverlay", at = @At("STORE"), name = "slot")
    public TextureAtlasSprite setPortalOverlayTexture(TextureAtlasSprite slot) {
        final CustomPortalBlock customPortalBlock = getCustomPortalBlock();
        if (customPortalBlock == null) return slot;

        return minecraft.getModelManager()
            .getBlockStateModelSet()
            .getParticleMaterial(customPortalBlock.defaultBlockState())
            .sprite();
    }
}
