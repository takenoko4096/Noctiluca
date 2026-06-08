package io.github.takenoko4096.noctiluca.mixin;

import io.github.takenoko4096.noctiluca.Noctiluca;
import io.github.takenoko4096.noctiluca.portal.CustomPortal;
import io.github.takenoko4096.noctiluca.portal.PortalIgnitionSource;
import io.github.takenoko4096.noctiluca.portal.PortalType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {
    @Inject(method = "onPlace", at = @At("HEAD"), cancellable = true)
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston, CallbackInfo ci) {
        if (level.isClientSide()) return;
        final CustomPortal portal = PortalType.Companion.getIgnitablePortal(level, pos);
        if (portal == null) return;
        portal.ignite(level, PortalIgnitionSource.Companion.block(state.getBlock()));
        ci.cancel();
    }

    // @Inject(method = "canBePlacedAt")
}
