package dev.runefox.jedt.test;

import dev.runefox.jedt.api.gametest.GameTestEvents;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestListener;

public class TestEventsListener implements GameTestListener {
    public static final TestEventsListener INSTANCE = new TestEventsListener();

    @Override
    public void testStructureLoaded(GameTestInfo gameTestInfo) {
        GameTestEvents.TEST_STRUCTURE_LOADED.invoker().onTestStructureLoaded(gameTestInfo);
    }

    @Override
    public void testPassed(GameTestInfo gameTestInfo) {
        GameTestEvents.TEST_PASSED.invoker().onTestPassed(gameTestInfo);
    }

    @Override
    public void testFailed(GameTestInfo gameTestInfo) {
        GameTestEvents.TEST_FAILED.invoker().onTestFailed(gameTestInfo);
    }
}
