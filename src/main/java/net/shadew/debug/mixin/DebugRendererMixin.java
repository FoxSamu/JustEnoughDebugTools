package net.shadew.debug.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.shadew.debug.render.DebugRenderers;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
    @Inject(method = "clear", at = @At("HEAD"))
    private void onReset(CallbackInfo info) {
        DebugRenderers.clear();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(PoseStack pose, MultiBufferSource.BufferSource buffSource, double cameraX, double cameraY, double cameraZ, CallbackInfo info) {
        DebugRenderers.render(pose, buffSource, cameraX, cameraY, cameraZ);
    }
}
