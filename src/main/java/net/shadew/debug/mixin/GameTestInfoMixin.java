package net.shadew.debug.mixin;

import net.minecraft.gametest.framework.GameTestInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.shadew.debug.api.gametest.GameTestEvents;

@Mixin(GameTestInfo.class)
public class GameTestInfoMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructorHook(CallbackInfo info) {
        GameTestInfo.class.cast(this).addListener(GameTestEvents.TEST_LISTENER);
    }
}
