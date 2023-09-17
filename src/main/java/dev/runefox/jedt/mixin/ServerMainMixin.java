package dev.runefox.jedt.mixin;

import dev.runefox.jedt.Debug;
import dev.runefox.jedt.test.GameTestServerStarter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.server.Main.class)
public class ServerMainMixin {
    // Injection into 'public static void main', so we don't have to remap
    @Inject(method = "main", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onMain(String[] args, CallbackInfo info) {
        boolean test = Debug.GAMETEST;
        if (test) {
            String testMod = System.getProperty("jedt.test_config");
            GameTestServerStarter.startServer(args, testMod);
            info.cancel();
        }
    }
}
