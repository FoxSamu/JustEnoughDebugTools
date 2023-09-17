package dev.runefox.jedt.mixin;

import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("InvalidInjectorMethodSignature")
@Mixin(targets = "net.minecraft.gametest.framework.ReportGameListener")
public class ReportGameListenerMixin {
    private static final Logger LOGGER = LogManager.getLogger("ReportGameListener");

    /*
     * The default implementation uses a GameTestInfo instance stored as a field of ReportGameListener, which might be
     * null and cause NPEs when trying to run tests of which no structure was saved.
     *
     * Since this is one of the listeners of GameTestListener, we are supplied an actual, non-null GameTestInfo
     * instance. Any other implemented listener method in ReportGameListener uses that parameter, so let's solve this
     * inconsistency by redirecting the usage of that field and using the passed parameter instead.
     *
     * Disabled for now: somehow Mixin AP fails to generate the proper refmap mapping
     */
//    @Redirect(
//        method = "testStructureLoaded",
//        at = @At(
//            value = "FIELD",
//            target = "Lnet/minecraft/gametest/framework/ReportGameListener;" +
//                         "originalTestInfo:Lnet/minecraft/gametest/framework/GameTestInfo;",
//            opcode = Opcodes.GETFIELD
//        )
//    )
//    private GameTestInfo redirectOriginalTestInfo(@Coerce GameTestListener instance, GameTestInfo testInfo) {
//        System.out.println("injection correct");
//        return testInfo;
//    }

    @Inject(method = {"spawnBeacon", "spawnLectern"}, at = @At("HEAD"), cancellable = true)
    private static void handleSpawnBeacon(GameTestInfo info, @Coerce Object v, CallbackInfo callback) {
        // This happens sometimes in these methods, crashing the game, so let's just prevent the method from running
        // instead
        if (info == null || info.getStructureBlockPos() == null) {
            if (v instanceof Block)
                LOGGER.error("Failed to spawn GameTest beacon as the structure was not correctly loaded");
            else
                LOGGER.error("Failed to spawn GameTest lectern as the structure was not correctly loaded");
            callback.cancel();
        }
    }
}
