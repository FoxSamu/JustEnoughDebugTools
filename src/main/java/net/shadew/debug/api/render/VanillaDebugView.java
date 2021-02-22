package net.shadew.debug.api.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class VanillaDebugView implements DebugView {
    private final Function<DebugRenderer, DebugRenderer.Renderer> renderer;
    private final BooleanSupplier enabled;

    public VanillaDebugView(Function<DebugRenderer, DebugRenderer.Renderer> renderer, BooleanSupplier enabled) {
        this.renderer = renderer;
        this.enabled = enabled;
    }

    @Override
    public void clear() {
        renderer.apply(MinecraftClient.getInstance().debugRenderer)
                .clear();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        renderer.apply(MinecraftClient.getInstance().debugRenderer)
                .render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }

    @Override
    public boolean isEnabled() {
        return enabled.getAsBoolean();
    }
}
