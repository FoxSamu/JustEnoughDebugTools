package net.shadew.debug.api.render;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public interface DebugRenderEvents {
    Event<DebugRenderEvents.Clear> CLEAR = EventFactory.createArrayBacked(
        DebugRenderEvents.Clear.class,
        callbacks -> () -> {
            for (DebugRenderEvents.Clear callback : callbacks) {
                callback.clear();
            }
        }
    );

    Event<DebugRenderEvents.Render> RENDER = EventFactory.createArrayBacked(
        DebugRenderEvents.Render.class,
        callbacks -> (matrices, vertexConsumers, cameraX, cameraY, cameraZ) -> {
            for (DebugRenderEvents.Render callback : callbacks) {
                callback.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
            }
        }
    );

    interface Clear {
        void clear();
    }

    interface Render {
        void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ);
    }
}
