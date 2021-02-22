package net.shadew.debug.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Set;

import net.shadew.debug.mixin.PathAccessor;

public class PathSerializer {
    public static void write(Path path, PacketByteBuf buf) {
        // Replicated write behaviour based on Path.fromBuffer
        PathAccessor accessor = (PathAccessor) path;
        buf.writeBoolean(accessor.reachesTarget());
        buf.writeInt(accessor.getCurrentNodeIndex());

        // Target path nodes, which are apparently not present on the server at all
        // Write 0 to indicate the size of this set of targets is 0, then there's no need to write anything else
        buf.writeInt(0);

        BlockPos target = accessor.getTarget();
        buf.writeInt(target.getX());
        buf.writeInt(target.getY());
        buf.writeInt(target.getZ());

        List<PathNode> nodes = accessor.getNodes();
        buf.writeInt(nodes.size());
        for (PathNode node : nodes) {
            write(node, buf);
        }

        PathNode[] open = accessor.getOpenList();
        buf.writeInt(open.length);
        for (PathNode node : open) {
            write(node, buf);
        }

        PathNode[] closed = accessor.getClosedList();
        buf.writeInt(closed.length);
        for (PathNode node : closed) {
            write(node, buf);
        }
    }

    public static void write(PathNode node, PacketByteBuf buf) {
        buf.writeInt(node.x);
        buf.writeInt(node.y);
        buf.writeInt(node.z);
        buf.writeFloat(node.pathLength);
        buf.writeFloat(node.penalty);
        buf.writeBoolean(node.visited);
        buf.writeInt(node.type.ordinal());
        buf.writeFloat(node.heapWeight);
    }
}
