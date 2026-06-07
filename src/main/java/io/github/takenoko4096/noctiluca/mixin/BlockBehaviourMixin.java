package io.github.takenoko4096.noctiluca.mixin;

import io.github.takenoko4096.noctiluca.math.Position3i;
import io.github.takenoko4096.noctiluca.portal.CustomPortal;
import io.github.takenoko4096.noctiluca.portal.PortalType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    @Inject(method = "onPlace", at = @At("HEAD"), cancellable = true)
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston, CallbackInfo info) {
        final Set<PortalType> candidates = PortalType.Companion.getCandidatesByIgnitionSourceBlock(state.getBlock());
        if (!state.getFluidState().is(Fluids.EMPTY)) {
            if (!state.getFluidState().isSource()) return;
        }
        for (PortalType candidate : candidates) {
            final CustomPortal portal = candidate.getPortalFinder().findPortal(level, Position3i.Companion.from(pos), CustomPortal::isIgnitable);
            if (portal != null) {
                portal.ignite(level);
                info.cancel();
                break;
            }
        }
    }
}
