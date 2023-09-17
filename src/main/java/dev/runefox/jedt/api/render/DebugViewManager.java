package dev.runefox.jedt.api.render;

import net.fabricmc.api.ClientModInitializer;

import java.util.ArrayList;
import java.util.List;

public class DebugViewManager {
    private static final List<DebugView> DEBUG_VIEWS = new ArrayList<>();

    @Deprecated // Do not use, internal
    public static final ClientModInitializer INIT = () -> {
        DebugRenderEvents.RENDER.register(
            (matrices, vertexConsumers, cameraX, cameraY, cameraZ) -> DEBUG_VIEWS.forEach(view -> {
                if (view.isEnabled()) {
                    view.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
                }
            })
        );
        DebugRenderEvents.CLEAR.register(
            () -> DEBUG_VIEWS.forEach(DebugView::clear)
        );
    };

    public static void register(DebugView view) {
        DEBUG_VIEWS.add(view);
    }
}
