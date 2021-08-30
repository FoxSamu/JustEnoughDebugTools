package net.shadew.debug;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.entrypoint.minecraft.hooks.EntrypointUtils;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.StructureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.shadew.debug.api.DebugInitializer;
import net.shadew.debug.api.DebugStatusInitializer;
import net.shadew.debug.impl.status.ServerDebugStatusImpl;
import net.shadew.debug.test.GameTestIntegration;
import net.shadew.debug.test.ModTestConfig;
import net.shadew.debug.test.RuntimeTestConfig;

public class Debug implements ModInitializer {
    public static final boolean GAMETEST = Boolean.parseBoolean(System.getProperty("jedt.gametest"));
    public static final boolean UWU = Boolean.parseBoolean(System.getProperty("jedt.uwu"));

    public static final Logger LOGGER = LogManager.getLogger();

    private static ServerDebugStatusImpl.Builder serverDebugStatusBuilder;
    public static ServerDebugStatusImpl serverDebugStatus;

    static void loadClientTests() {
        loadTests().getAllTestMethods("_runtime", "_client").forEach(GameTestRegistry::register);
        LOGGER.info("Loaded {} tests", GameTestRegistry.getAllTestFunctions().size());
    }

    static void loadServerTests() {
        loadTests().getAllTestMethods("_runtime", "_server").forEach(GameTestRegistry::register);
        LOGGER.info("Loaded {} tests", GameTestRegistry.getAllTestFunctions().size());
    }

    private static RuntimeTestConfig loadTests() {
        RuntimeTestConfig rtTestConfig = new RuntimeTestConfig();
        for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
            ModTestConfig config = GameTestIntegration.loadModTestConfig(container);
            if (config == null) {
                LOGGER.error("Failed to load jedt.tests.json in mod {}", container.getMetadata().getId());
            }
            rtTestConfig.addModConfig(config);
        }
        return rtTestConfig;
    }

    @Override
    public void onInitialize() {
        // Enable GameTest
        // SharedConstants.IS_RUNNING_IN_IDE = true;

        // ... but that's a mixin now

        serverDebugStatus = createStatusInstance();

        if (!GAMETEST) {
            String testStructuresDir = System.getProperty("jedt.test_structures_path");
            if (testStructuresDir != null)
                StructureUtils.testStructuresDir = testStructuresDir;
        }

        EntrypointUtils.invoke(
            "jedt:main", DebugInitializer.class,
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
                "jedt:status", DebugStatusInitializer.class,
                init -> init.onInitializeDebugStatus(serverDebugStatusBuilder)
            );
        }

        return serverDebugStatusBuilder.build();
    }
}
