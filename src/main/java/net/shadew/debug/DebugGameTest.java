package net.shadew.debug;

import net.minecraft.gametest.framework.GlobalTestReporter;

import java.io.File;

import net.shadew.debug.api.GameTestInitializer;
import net.shadew.debug.api.gametest.GameTestCIUtil;
import net.shadew.debug.api.gametest.GameTestEvents;
import net.shadew.debug.test.ProperJUnitLikeTestReporter;

public class DebugGameTest implements GameTestInitializer {
    @Override
    public void initializeGameTestServer() throws Exception {
        GlobalTestReporter.replaceWith(new ProperJUnitLikeTestReporter(new File("test_results.xml")));
        GameTestEvents.TEST_SERVER_DONE.register(server -> {
            GameTestCIUtil.exportTestWorldAsZip(server, new File("test_world.zip"));
        });
    }
}
