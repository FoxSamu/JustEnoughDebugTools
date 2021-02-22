package net.shadew.debug.util;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;

import net.shadew.debug.Debug;
import net.shadew.debug.DebugClient;
import net.shadew.debug.api.status.DebugStatusEvents;
import net.shadew.debug.mixin.GameRulesIntRuleAccessor;

public class DebugNetworkClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(DebugNetwork.GAME_RULES_PACKET_ID, (client, handler, buf, responseSender) -> {
            Map<String, Integer> values = new HashMap<>();
            int c = buf.readInt();
            for (int i = 0; i < c; i ++) {
                int t = buf.readByte();
                String k = buf.readString();
                int v = t == 0 ? buf.readByte() : buf.readInt();
                values.put(k, v);
            }

            client.execute(() -> {
                if (client.world == null) {
                    return;
                }

                GameRules gameRules = client.world.getGameRules();
                GameRules.accept(new GameRules.Visitor() {
                    @Override
                    public void visitBoolean(GameRules.Key<GameRules.BooleanRule> key, GameRules.Type<GameRules.BooleanRule> type) {
                        String name = key.getName();
                        gameRules.get(key).set(values.get(name) != 0, null);
                    }

                    @Override
                    public void visitInt(GameRules.Key<GameRules.IntRule> key, GameRules.Type<GameRules.IntRule> type) {
                        String name = key.getName();
                        ((GameRulesIntRuleAccessor) gameRules.get(key)).setRuleValue(values.get(name));
                    }
                });
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(DebugNetwork.SERVER_STATUS_PACKET_ID, (client, handler, buf, responseSender) -> {
            DebugClient.serverDebugStatus.deserialize(buf);
            client.execute(() -> {
                DebugStatusEvents.CLIENT_READY.invoker().ready(DebugClient.serverDebugStatus);
                DebugClient.serverDebugStatus.log(DebugClient.LOGGER);
            });
        });
    }
}
