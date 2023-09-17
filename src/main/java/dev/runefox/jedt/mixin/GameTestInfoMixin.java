package dev.runefox.jedt.mixin;

import dev.runefox.jedt.test.TestEventsListener;
import net.minecraft.gametest.framework.GameTestInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameTestInfo.class)
public class GameTestInfoMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructorHook(CallbackInfo info) {
        GameTestInfo.class.cast(this).addListener(TestEventsListener.INSTANCE);
    }
}
