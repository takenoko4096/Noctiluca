package io.github.takenoko4096.noctiluca.mixin;

import io.github.takenoko4096.noctiluca.math.Position3i;
import io.github.takenoko4096.noctiluca.portal.CustomPortal;
import io.github.takenoko4096.noctiluca.portal.PortalIgnitionSource;
import io.github.takenoko4096.noctiluca.portal.PortalType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {
    @Inject(method = "onPlace", at = @At("HEAD"), cancellable = true)
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston, CallbackInfo ci) {
        if (level.isClientSide()) return;
        final CustomPortal portal = PortalType.Companion.getIgnitablePortal(level, pos);
        if (portal == null) return;
        if (!state.canSurvive(level, pos)) {
            level.removeBlock(pos, false);
        }
        portal.ignite(level, PortalIgnitionSource.Companion.block$Noctiluca(state.getBlock()));
        ci.cancel();
    }

    @Inject(method = "isPortal", at = @At("HEAD"), cancellable = true)
    private static void isPortal(Level level, BlockPos pos, Direction forwardDirection, CallbackInfoReturnable<Boolean> cir) {
        final Set<PortalType> types = PortalType.Companion.getCandidatesByIgnitionSourceBlock(s -> {
            return s.getSource() instanceof BaseFireBlock;
        });

        for (final PortalType type : types) {
            final CustomPortal portal = type.getPortalFinder().findPortal(
                level,
                Position3i.Companion.from(pos),
                CustomPortal::isIgnitable
            );

            if (portal != null) {
                cir.setReturnValue(true);
                break;
            }
        }
    }
}
