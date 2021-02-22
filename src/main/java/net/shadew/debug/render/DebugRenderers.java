package net.shadew.debug.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.commons.lang3.mutable.MutableBoolean;

import net.shadew.debug.api.render.DebugRenderEvents;
import net.shadew.debug.api.render.DebugView;
import net.shadew.debug.api.render.DebugViewManager;
import net.shadew.debug.api.render.VanillaDebugView;

public class DebugRenderers {
    public static final MutableBoolean PATHFINDING_ENABLED = new MutableBoolean(false);
    public static final MutableBoolean NEIGHBOR_UPDATES_SHOWN = new MutableBoolean(false);
    public static final MutableBoolean HEIGHTMAPS_SHOWN = new MutableBoolean(false);
    public static final MutableBoolean FLUID_LEVELS_SHOWN = new MutableBoolean(false);
    public static final MutableBoolean COLLISIONS_SHOWN = new MutableBoolean(false);

    public static final DebugView PATHFINDING = register(new VanillaDebugView(r -> r.pathfindingDebugRenderer, PATHFINDING_ENABLED::booleanValue));
    public static final DebugView NEIGHBOR_UPDATES = register(new VanillaDebugView(r -> r.neighborUpdateDebugRenderer, NEIGHBOR_UPDATES_SHOWN::booleanValue));
    public static final DebugView HEIGHTMAPS = register(new VanillaDebugView(r -> r.heightmapDebugRenderer, HEIGHTMAPS_SHOWN::booleanValue));
    public static final DebugView FLUID_LEVELS = register(new FluidsDebugView(MinecraftClient.getInstance()));
    public static final DebugView COLLISIONS = register(new VanillaDebugView(r -> r.collisionDebugRenderer, COLLISIONS_SHOWN::booleanValue));

    public static void reset() {
        DebugRenderEvents.CLEAR.invoker().clear();
    }

    public static void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        DebugRenderEvents.RENDER.invoker().render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }

    private static <V extends DebugView> V register(V view) {
        DebugViewManager.register(view);
        return view;
    }
}
