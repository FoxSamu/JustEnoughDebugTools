package net.shadew.debug.mixin;

import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestListener;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
     */
    @Redirect(
        method = "testStructureLoaded",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/gametest/framework/ReportGameListener;" +
                         "originalTestInfo:Lnet/minecraft/gametest/framework/GameTestInfo;",
            opcode = Opcodes.GETFIELD
        )
    )
    private GameTestInfo redirectOriginalTestInfo(@Coerce GameTestListener instance, GameTestInfo testInfo) {
        return testInfo;
    }

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
