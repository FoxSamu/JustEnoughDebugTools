package net.shadew.debug.mixin;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.shadew.debug.Debug;
import net.shadew.debug.api.status.StandardStatusKeys;
import net.shadew.debug.util.PathSerializer;

@Mixin(DebugInfoSender.class)
public class DebugInfoSenderMixin {
    // These are all a couple of methods whose contents were removed by Mojang
    // This mixin is dedicated to add these contents again
    // Injects do work, although overwrites are much easier and never is the target class intended to send the dedicated
    // packets twice - which can happen if multiple mods (or multiple instances of this mod) try to inject the same
    // method contents. Hence this mixin uses overwrites, to force that the methods only do what they are supposed to
    // do. Supposedly nobody is going to mixin into this class for some other reason than just re-adding the method
    // contents that Mojang removed.

    @Overwrite
    public static void sendPathfindingData(World world, MobEntity mob, @Nullable Path path, float nodeReachProximity) {
        if (path == null || !(world instanceof ServerWorld)) {
            return;
        }

        if (world.getGameRules().getBoolean(GameRules.REDUCED_DEBUG_INFO)) {
            return;
        }

        if (!Debug.serverDebugStatus.getStatus(StandardStatusKeys.SEND_PATHFINDING_INFO)) {
            return;
        }

        // Replicated write behaviour from ClientPlayNetworkHandler.onCustomPayload
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(mob.getEntityId());
        buf.writeFloat(nodeReachProximity);
        PathSerializer.write(path, buf);
        sendToAllWatching((ServerWorld) world, buf, CustomPayloadS2CPacket.DEBUG_PATH, mob);
    }

    @Overwrite
    public static void sendNeighborUpdate(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld)) {
            return;
        }

        if (world.getGameRules().getBoolean(GameRules.REDUCED_DEBUG_INFO)) {
            return;
        }

        if (!Debug.serverDebugStatus.getStatus(StandardStatusKeys.SEND_NEIGHBOR_UPDATES)) {
            return;
        }

        // Replicated write behaviour from ClientPlayNetworkHandler.onCustomPayload
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarLong(world.getTime());
        buf.writeBlockPos(pos);
        sendToAllWatching((ServerWorld) world, buf, CustomPayloadS2CPacket.DEBUG_NEIGHBORS_UPDATE, pos);
    }

    @Overwrite
    public static void sendChunkWatchingChange(ServerWorld world, ChunkPos pos) {

    }

    private static void sendToAllWatching(ServerWorld world, PacketByteBuf buf, Identifier channel, Entity watch) {
        Packet<?> packet = new CustomPayloadS2CPacket(channel, buf);
        world.toServerWorld().getChunkManager().sendToOtherNearbyPlayers(watch, packet);
    }

    private static void sendToAllWatching(ServerWorld world, PacketByteBuf buf, Identifier channel, BlockPos watch) {
        Packet<?> packet = new CustomPayloadS2CPacket(channel, buf);
        int cx = watch.getX() / 16;
        int cz = watch.getZ() / 16;

        ThreadedAnvilChunkStorage storage = world.toServerWorld().getChunkManager().threadedAnvilChunkStorage;
        storage.getPlayersWatchingChunk(new ChunkPos(cx, cz), false).forEach(
            player -> player.networkHandler.sendPacket(packet)
        );
    }
}
