package net.shadew.debug.api.gametest;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.server.MinecraftServer;

public interface GameTestEvents {
    Event<TestStructureLoaded> TEST_STRUCTURE_LOADED = EventFactory.createArrayBacked(
        TestStructureLoaded.class,
        callbacks -> info -> {
            for (TestStructureLoaded callback : callbacks) {
                callback.onTestStructureLoaded(info);
            }
        }
    );
    Event<TestPassed> TEST_PASSED = EventFactory.createArrayBacked(
        TestPassed.class,
        callbacks -> info -> {
            for (TestPassed callback : callbacks) {
                callback.onTestPassed(info);
            }
        }
    );
    Event<TestFailed> TEST_FAILED = EventFactory.createArrayBacked(
        TestFailed.class,
        callbacks -> info -> {
            for (TestFailed callback : callbacks) {
                callback.onTestFailed(info);
            }
        }
    );
    Event<TestServerDone> TEST_SERVER_DONE = EventFactory.createArrayBacked(
        TestServerDone.class,
        callbacks -> server -> {
            for (TestServerDone callback : callbacks) {
                callback.onTestServerDone(server);
            }
        }
    );

    interface TestStructureLoaded {
        void onTestStructureLoaded(GameTestInfo info);
    }

    interface TestPassed {
        void onTestPassed(GameTestInfo info);
    }

    interface TestFailed {
        void onTestFailed(GameTestInfo info);
    }

    interface TestServerDone {
        void onTestServerDone(MinecraftServer server);
    }
}
