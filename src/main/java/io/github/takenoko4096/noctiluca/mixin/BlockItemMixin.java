package io.github.takenoko4096.noctiluca.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.takenoko4096.noctiluca.Noctiluca;
import io.github.takenoko4096.noctiluca.portal.CustomPortal;
import io.github.takenoko4096.noctiluca.portal.PortalIgnitionSource;
import io.github.takenoko4096.noctiluca.portal.PortalType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(method = "place", at = @At("TAIL"))
    public void place(BlockPlaceContext placeContext, CallbackInfoReturnable<InteractionResult> cir, @Local(name = "pos") BlockPos pos) {
        final BlockState state = placeContext.getLevel().getBlockState(pos);
        Noctiluca.INSTANCE.getLogger().info("placed at: {}, {}", pos, state);

        final CustomPortal portal = PortalType.Companion.getIgnitablePortal(placeContext.getLevel(), pos);
        if (portal == null) return;
        portal.ignite(placeContext.getLevel(), PortalIgnitionSource.Companion.block(state.getBlock()));
    }
}
