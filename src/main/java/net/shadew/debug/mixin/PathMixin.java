package net.shadew.debug.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(Path.class)
public class PathMixin {
    @Shadow
    private Set<Target> targetNodes;
    @Shadow
    @Final
    private boolean reached;
    @Shadow
    private int nextNodeIndex;
    @Shadow
    @Final
    private BlockPos target;
    @Shadow
    @Final
    private List<Node> nodes;
    @Shadow
    private Node[] openSet;
    @Shadow
    private Node[] closedSet;

    @Inject(method = "writeToStream", at = @At("HEAD"), cancellable = true)
    private void onWriteToStream(FriendlyByteBuf buf, CallbackInfo info) {
        // Minecraft does not serialize the path if 'targetNodes' is null or empty. We want to write the path anyway so
        // just write here and cancel this method.
        // TODO Maybe later add the option to add path debug info via an API and an option to show debugging paths only

        buf.writeBoolean(reached);
        buf.writeInt(nextNodeIndex);

        if (targetNodes != null) {
            buf.writeInt(targetNodes.size());
            targetNodes.forEach(target -> target.writeToStream(buf));
        } else {
            buf.writeInt(0); // treat as empty, write length 0 and then nothing
        }

        buf.writeInt(target.getX());
        buf.writeInt(target.getY());
        buf.writeInt(target.getZ());

        buf.writeInt(nodes.size());

        for (Node node : nodes) {
            node.writeToStream(buf);
        }

        buf.writeInt(openSet.length);
        for (Node node3 : openSet) {
            node3.writeToStream(buf);
        }

        buf.writeInt(closedSet.length);
        for (Node node3 : closedSet) {
            node3.writeToStream(buf);
        }

        info.cancel();
    }
}
