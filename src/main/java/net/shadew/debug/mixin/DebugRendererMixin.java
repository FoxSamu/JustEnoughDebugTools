package net.shadew.debug.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.shadew.debug.render.DebugRenderers;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
    @Inject(method = "reset", at = @At("HEAD"))
    private void onReset(CallbackInfo info) {
        DebugRenderers.reset();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo info) {
        DebugRenderers.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }
}
