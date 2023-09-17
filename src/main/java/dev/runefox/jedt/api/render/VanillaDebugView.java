package dev.runefox.jedt.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

public record VanillaDebugView(Function<DebugRenderer, DebugRenderer.SimpleDebugRenderer> renderer, BooleanSupplier enabled) implements DebugView {
    @Override
    public void clear() {
        renderer.apply(Minecraft.getInstance().debugRenderer).clear();
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffSource, double cameraX, double cameraY, double cameraZ) {
        renderer.apply(Minecraft.getInstance().debugRenderer).render(pose, buffSource, cameraX, cameraY, cameraZ);
    }

    @Override
    public boolean isEnabled() {
        return enabled.getAsBoolean();
    }
}
