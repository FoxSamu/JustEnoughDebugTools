package net.shadew.debug.util;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.apache.commons.lang3.mutable.MutableInt;

import net.shadew.debug.impl.status.ServerDebugStatusImpl;

public class DebugNetwork implements ModInitializer {
    public static final Identifier GAME_RULES_PACKET_ID = new Identifier("debug:game_rules");
    public static final Identifier SERVER_STATUS_PACKET_ID = new Identifier("debug:server_status");

    @Override
    public void onInitialize() {

    }

    public static void sendServerStatus(ServerDebugStatusImpl status, ServerPlayerEntity entity) {
        PacketByteBuf buf = PacketByteBufs.create();
        status.serialize(buf);
        ServerPlayNetworking.send(entity, SERVER_STATUS_PACKET_ID, buf);
    }

    public static void sendServerStatus(ServerDebugStatusImpl status, MinecraftServer server) {
        PacketByteBuf buf = PacketByteBufs.create();
        status.serialize(buf);
        server.getPlayerManager()
              .getPlayerList()
              .forEach(player -> ServerPlayNetworking.send(player, SERVER_STATUS_PACKET_ID, buf));
    }

    private static PacketByteBuf serializeGameRules(MinecraftServer server) {
        MutableInt count = new MutableInt(0);
        PacketByteBuf buf = PacketByteBufs.create();
        PacketByteBuf rulesBuf = PacketByteBufs.create();

        GameRules rules = server.getGameRules();
        GameRules.accept(new GameRules.Visitor() {
            @Override
            public void visitBoolean(GameRules.Key<GameRules.BooleanRule> key, GameRules.Type<GameRules.BooleanRule> type) {
                count.increment();
                rulesBuf.writeByte(0);
                rulesBuf.writeString(key.getName());
                rulesBuf.writeByte(rules.getBoolean(key) ? 1 : 0);
            }

            @Override
            public void visitInt(GameRules.Key<GameRules.IntRule> key, GameRules.Type<GameRules.IntRule> type) {
                count.increment();
                rulesBuf.writeByte(1);
                rulesBuf.writeString(key.getName());
                rulesBuf.writeInt(rules.getInt(key));
            }
        });
        buf.writeInt(count.getValue());
        buf.writeBytes(rulesBuf);

        rulesBuf.release();

        return buf;
    }

    public static void sendGameRules(MinecraftServer server) {
        PacketByteBuf buf = serializeGameRules(server);
        server.getPlayerManager()
              .getPlayerList()
              .forEach(player -> ServerPlayNetworking.send(player, GAME_RULES_PACKET_ID, buf));
    }

    public static void sendGameRules(ServerPlayerEntity player) {
        PacketByteBuf buf = serializeGameRules(player.server);
        ServerPlayNetworking.send(player, GAME_RULES_PACKET_ID, buf);
    }
}
