package io.github.takenoko4096.noctiluca.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.takenoko4096.noctiluca.registry.block.CustomFireBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Level.class)
public abstract class LevelMixin {
    @WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;levelEvent(ILnet/minecraft/core/BlockPos;I)V"))
    private void destroyBlock(Level level, int type, BlockPos pos, int data, Operation<Void> original, @Local(name = "blockState") BlockState blockState) {
        if (!(blockState.getBlock() instanceof CustomFireBlock)) {
            original.call(level, type, pos, data);
        }
    }
}
