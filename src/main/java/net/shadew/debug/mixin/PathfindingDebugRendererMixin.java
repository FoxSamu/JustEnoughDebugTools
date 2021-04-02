package net.shadew.debug.mixin;

import net.minecraft.client.renderer.debug.PathfindingRenderer;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.shadew.debug.render.DebugRenderers;

@Mixin(PathfindingRenderer.class)
public class PathfindingDebugRendererMixin {
    @Inject(method = "addPath", at = @At("HEAD"), cancellable = true)
    private void onAddPath(int id, Path path, float pathNodeSize, CallbackInfo info) {
        DebugRenderers.PATHFINDING.addPath(id, path, pathNodeSize);
        // Cancel to not consume memory in the default pathfinder debug renderer
        // This memory is only deleted on render, which is not called
        info.cancel();
    }
}
