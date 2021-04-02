package net.shadew.debug.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public interface DebugView {
    void clear();
    void render(PoseStack pose, MultiBufferSource buffSource, double cameraX, double cameraY, double cameraZ);
    boolean isEnabled();
}
