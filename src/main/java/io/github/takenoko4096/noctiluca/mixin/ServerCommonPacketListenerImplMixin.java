package io.github.takenoko4096.noctiluca.mixin;

import com.mojang.authlib.GameProfile;
import io.github.takenoko4096.noctiluca.ui.dialog.DynamicDialog;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
abstract class ServerCommonPacketListenerImplMixin {
    @Shadow protected abstract GameProfile playerProfile();

    @Shadow @Final protected MinecraftServer server;

    // PacketUtils.ensureRunningOnSameThread(packet, this, this.server.packetProcessor());の直後に呼ぶことでサーバースレッドから呼び出されたときにインジェクトすることを保証
    @Inject(method = "handleCustomClickAction(Lnet/minecraft/network/protocol/common/ServerboundCustomClickActionPacket;)V", at= @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;handleCustomClickAction(Lnet/minecraft/resources/Identifier;Ljava/util/Optional;)V"))
    public void injectHandleCustomClickAction(ServerboundCustomClickActionPacket packet, CallbackInfo info) {
        final Player player = server.getPlayerList().getPlayer(playerProfile().id());
        if (player == null) return;
        DynamicDialog.Companion.invokeOnClick(player, packet.id(), packet.payload().orElse(null));
    }
}
