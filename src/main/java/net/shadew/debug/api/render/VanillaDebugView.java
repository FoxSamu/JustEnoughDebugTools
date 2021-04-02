package net.shadew.debug.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class VanillaDebugView implements DebugView {
    private final Function<DebugRenderer, DebugRenderer.SimpleDebugRenderer> renderer;
    private final BooleanSupplier enabled;

    public VanillaDebugView(Function<DebugRenderer, DebugRenderer.SimpleDebugRenderer> renderer, BooleanSupplier enabled) {
        this.renderer = renderer;
        this.enabled = enabled;
    }

    @Override
    public void clear() {
        renderer.apply(Minecraft.getInstance().debugRenderer)
                .clear();
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffSource, double cameraX, double cameraY, double cameraZ) {
        renderer.apply(Minecraft.getInstance().debugRenderer)
                .render(pose, buffSource, cameraX, cameraY, cameraZ);
    }

    @Override
    public boolean isEnabled() {
        return enabled.getAsBoolean();
    }
}
