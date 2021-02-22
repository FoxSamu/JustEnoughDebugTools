package net.shadew.debug.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.shadew.debug.Debug;
import net.shadew.debug.util.DebugNetwork;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void onOnPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        DebugNetwork.sendServerStatus(Debug.serverDebugStatus, player);
    }
}
