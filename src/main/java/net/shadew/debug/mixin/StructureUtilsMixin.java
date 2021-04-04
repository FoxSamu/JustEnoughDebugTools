package net.shadew.debug.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureUtils.class)
public class StructureUtilsMixin {
    @Inject(method = "clearBlock", at = @At("HEAD"), cancellable = true)
    private static void onClearBlock(int y, BlockPos pos, ServerLevel level, CallbackInfo info) {
        // Default implementation is slow, here's a quick implementation
        if (pos.getY() < y) {
            if (pos.getY() < y - 1)
                level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 2);
            else
                level.setBlock(pos, Blocks.GRASS_BLOCK.defaultBlockState(), 2);
        } else {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
        }
        info.cancel();
    }
}
