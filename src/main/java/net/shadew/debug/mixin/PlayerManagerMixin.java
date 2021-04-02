package net.shadew.debug.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.shadew.debug.Debug;
import net.shadew.debug.util.DebugNetwork;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {
    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void onOnPlayerConnect(Connection connection, ServerPlayer player, CallbackInfo info) {
        DebugNetwork.sendServerStatus(Debug.serverDebugStatus, player);
    }
}
