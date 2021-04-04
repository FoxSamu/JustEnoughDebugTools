package net.shadew.debug;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.entrypoint.minecraft.hooks.EntrypointUtils;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.StructureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.shadew.debug.api.DebugInitializer;
import net.shadew.debug.api.DebugStatusInitializer;
import net.shadew.debug.impl.status.ServerDebugStatusImpl;
import net.shadew.debug.test.DebugTests;

public class Debug implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();

    private static ServerDebugStatusImpl.Builder serverDebugStatusBuilder;
    public static ServerDebugStatusImpl serverDebugStatus;

    @Override
    public void onInitialize() {
        // Enable GameTest
        // SharedConstants.IS_RUNNING_IN_IDE = true;

        GameTestRegistry.register(DebugTests.class);

        serverDebugStatus = createStatusInstance();
        String testStructuresDir = System.getProperty("jedt.test_structures_path");
        if (testStructuresDir != null)
            StructureUtils.testStructuresDir = testStructuresDir;

        EntrypointUtils.invoke(
            "debug:main", DebugInitializer.class,
            init -> init.onInitializeDebug(serverDebugStatus)
        );

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            if (!server.isDedicatedServer()) {
                serverDebugStatus.resetAll();
            }
        });
    }

    public static ServerDebugStatusImpl createStatusInstance() {
        if (serverDebugStatusBuilder == null) {
            serverDebugStatusBuilder = new ServerDebugStatusImpl.Builder();
            new DefaultStatusInitializer().onInitializeDebugStatus(serverDebugStatusBuilder);
            EntrypointUtils.invoke(
                "debug:status", DebugStatusInitializer.class,
                init -> init.onInitializeDebugStatus(serverDebugStatusBuilder)
            );
        }

        return serverDebugStatusBuilder.build();
    }
}
