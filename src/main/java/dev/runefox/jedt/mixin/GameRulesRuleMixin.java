package dev.runefox.jedt.mixin;

import dev.runefox.jedt.Debug;
import dev.runefox.jedt.api.status.StandardStatusKeys;
import dev.runefox.jedt.util.DebugNetwork;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRules.Value.class)
public class GameRulesRuleMixin {
    @Inject(method = "onChanged", at = @At("HEAD"))
    private void onRuleChange(MinecraftServer server, CallbackInfo info) {
        if (server != null && Debug.serverDebugStatus.getStatus(StandardStatusKeys.GAME_RULE_SYNC))
            DebugNetwork.sendGameRules(server);
    }
}
