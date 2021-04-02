package net.shadew.debug.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;

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
        callbacks -> (pose, buffSource, cameraX, cameraY, cameraZ) -> {
            for (DebugRenderEvents.Render callback : callbacks) {
                callback.render(pose, buffSource, cameraX, cameraY, cameraZ);
            }
        }
    );

    interface Clear {
        void clear();
    }

    interface Render {
        void render(PoseStack pose, MultiBufferSource.BufferSource buffSource, double cameraX, double cameraY, double cameraZ);
    }
}
