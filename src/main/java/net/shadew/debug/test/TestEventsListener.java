package net.shadew.debug.test;

import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestListener;

import net.shadew.debug.api.gametest.GameTestEvents;

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
