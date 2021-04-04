package net.shadew.debug.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.shadew.debug.test.GameTestServerStarter;

@Mixin(net.minecraft.server.Main.class)
public class ServerMainMixin {
    @Inject(method = "main", at = @At("HEAD"), cancellable = true)
    private static void onMain(String[] args, CallbackInfo info) {
        String testMod = System.getProperty("jedt.test_mod");
        if (testMod != null) {
            GameTestServerStarter.startServer(args, testMod);
            info.cancel();
        }
    }
}
