package io.github.takenoko4096.noctiluca.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.takenoko4096.noctiluca.Noctiluca;
import io.github.takenoko4096.noctiluca.portal.CustomPortal;
import io.github.takenoko4096.noctiluca.portal.PortalIgnitionSource;
import io.github.takenoko4096.noctiluca.portal.PortalType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class BucketItemMixin {
    @Shadow
    @Final
    private Fluid content;

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BucketItem;checkExtraContent(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/BlockPos;)V"))
    public void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, @Local(name = "placePos") BlockPos placePos) {
        final BlockState state = level.getBlockState(placePos);

        Noctiluca.INSTANCE.getLogger().info("fluid placed at: {}, {}", placePos, state);
        final CustomPortal portal = PortalType.Companion.getIgnitablePortal(level, placePos);
        if (portal == null) return;
        portal.ignite(level, PortalIgnitionSource.Companion.block(
            content.defaultFluidState().createLegacyBlock().getBlock()
        ));
    }
}
