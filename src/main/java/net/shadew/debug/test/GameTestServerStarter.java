package net.shadew.debug.test;

import com.mojang.logging.LogUtils;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.impl.game.minecraft.Hooks;
import net.minecraft.CrashReport;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.commands.Commands;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.GlobalTestReporter;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import net.shadew.debug.api.GameTestInitializer;
import net.shadew.debug.util.PathUtil;

public class GameTestServerStarter {
    private static final Logger LOGGER = LogUtils.getLogger();


    /*
     * Ok ok ok, this is a bit of a hacky unit of code that is performing the complete server startup when we want to
     * run a GameTestServer instead. This is directly mixed in net.minecraft.server.Main, at the HEAD of the main
     * method. That means that any hooks made into this class that are made by Fabric Loader are now not being called.
     *
     * To get modloading going properly, I have to replicate the tricks that Fabric does to initiate mod loading. This
     * sounds hacky, but there's no other way to do it, and if we don't do it we instantly break compatibility with all
     * mods, including ourselves, when we try to run a GameTestServer.
     *
     * I know exactly what I'm doing here.
     */
    @SuppressWarnings("deprecation")
    public static void startServer(String[] args, String config) {
        SharedConstants.tryDetectVersion();

        OptionParser optionParser = new OptionParser();
        OptionSpec<Void> helpSpec = optionParser.accepts("help").forHelp();
        OptionSpec<String> worldSpec = optionParser.accepts("world").withRequiredArg();

        try {
            OptionSet options = optionParser.parse(args);
            if (options.has(helpSpec)) {
                optionParser.printHelpOn(System.err);
                return;
            }


            /*
             * Load configuration
             */

            // Load config
            File universe = new File(".");
            Path configPath = PathUtil.resolve(universe.toPath(), config);

            if (!Files.exists(configPath)) {
                LOGGER.error("Could not find test config file at {}", configPath);
                System.exit(1);
                return;
            }

            LOGGER.info("Loading runtime test configuration from {}", configPath);

            RuntimeTestConfig rtConfig = GameTestIntegration.loadRuntimeTestConfig(configPath);
            if (rtConfig == null) {
                // GLaDOS is sad now
                LOGGER.error("Failed to load test config file at {}, cannot continue", configPath);
                System.exit(1);
                return;
            }

            // Load mod configs
            for (ModContainer container : FabricLoader.INSTANCE.getAllMods()) {
                if (rtConfig.includesMod(container.getMetadata().getId())) {
                    ModTestConfig modConfig = GameTestIntegration.loadModTestConfig(container);
                    if (modConfig == null) {
                        LOGGER.error("Failed to load jedt.tests.json in mod {}", container.getMetadata().getId());
                    }
                    rtConfig.addModConfig(modConfig);
                }
            }

            /*
             * Pre-load server
             */
            CrashReport.preload();
            Bootstrap.bootStrap();
            Bootstrap.validate();
            // Util.startTimerHackThread();

            // RegistryAccess.RegistryHolder registries = RegistryAccess.builtin();

            Path path2 = Paths.get("server.properties");
            DedicatedServerSettings settings = new DedicatedServerSettings(path2);
            settings.forceSave();


            /*
             * Setup level
             */
            // TODO Move level name to runtime config
            String levelName = Optional.ofNullable(options.valueOf(worldSpec)).orElse("gametestworld");
            LevelStorageSource storageSrc = LevelStorageSource.createDefault(universe.toPath());
            LevelStorageSource.LevelStorageAccess storageAcc = storageSrc.createAccess(levelName);

            LevelSummary summary = storageAcc.getSummary();
            if (summary != null) {
                if (summary.requiresManualConversion()) {
                    LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
                    return;
                }

                if (!summary.isCompatible()) {
                    LOGGER.info("This world was created by an incompatible version.");
                    return;
                }
            }

            /*
             * Initiate mods
             */

            // This is the moment we start to load data packs, we must now load mods.
            // We don't have a game instance yet, we set this at the end.
            //
            // Since we're completely skipping Fabric's hook, let's do it manually
            Hooks.startServer(null, null);

            // Only call the game test entrypoints that are part of the selected mod
            List<EntrypointContainer<GameTestInitializer>> entrypointContainers
                = FabricLoader.INSTANCE.getEntrypointContainers("debug:gametest", GameTestInitializer.class);

            for (EntrypointContainer<GameTestInitializer> ep : entrypointContainers) {
                if (rtConfig.modConfig(ep.getProvider().getMetadata().getId()) != null) {
                    ep.getEntrypoint().initializeGameTestServer();
                }
            }

            /*
             * Load datapacks
             */
            Optional<Path> dataPacksPath = rtConfig.datapacksPath(universe.toPath());

            PackRepository packRepository = dataPacksPath.map(
                path -> new PackRepository(
                    new ServerPacksSource(),
                    new FolderRepositorySource(path, PackType.SERVER_DATA, PackSource.SERVER),
                    new FolderRepositorySource(storageAcc.getLevelPath(LevelResource.DATAPACK_DIR), PackType.SERVER_DATA, PackSource.WORLD)
                )
            ).orElseGet(
                () -> new PackRepository(
                    new ServerPacksSource(),
                    new FolderRepositorySource(storageAcc.getLevelPath(LevelResource.DATAPACK_DIR), PackType.SERVER_DATA, PackSource.WORLD)
                )
            );


            /*
             * Setup GameTest
             */

            // This is needed for the server to run the tests
            SharedConstants.IS_RUNNING_IN_IDE = true;
            Path serverDir = universe.toPath();

            // Set test structures directory
            rtConfig.testStructuresPath(serverDir)
                    .ifPresent(path -> StructureUtils.testStructuresDir = path.toString());

            // Initiate TestReporter
            GlobalTestReporter.replaceWith(rtConfig.instantiateReporter(serverDir));

            // Register methods
            String[] sets = rtConfig.allModSets()
                                    .filter(rtConfig::includesSet)
                                    .distinct()
                                    .toArray(String[]::new);
            rtConfig.allTestMethods(sets)
                    .forEach(GameTestRegistry::register);

            /*
             * Start the server
             */
            LOGGER.info("Starting JEDT game test server");

            DebugGameTestServer server = MinecraftServer.spin(thread -> DebugGameTestServer.create(
                thread,
                universe,
                storageAcc,
                packRepository,
                rtConfig
            ));

            // Set the game instance, we must do this here since we had no game instance at modloading time
            // Yes this should not be called anywhere else than from the dedicated server ...
            //
            // ... but we are the dedicated server now!
            Hooks.setGameInstance(server);

            Thread shutdownThread = new Thread(() -> server.halt(true));
            shutdownThread.setName("Server Shutdown Thread");
            shutdownThread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
            Runtime.getRuntime().addShutdownHook(shutdownThread);
        } catch (Exception exc) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Failed to start the minecraft server", exc);
            System.exit(1);
        }
    }

    private static WorldLoader.InitConfig loadOrCreateConfig(LevelStorageSource.LevelStorageAccess levelStorageAccess, boolean bl, PackRepository packRepository) {
        WorldDataConfiguration worldDataConfiguration = levelStorageAccess.getDataConfiguration();
        WorldDataConfiguration worldDataConfiguration2;
        boolean bl2;
        if (worldDataConfiguration != null) {
            bl2 = false;
            worldDataConfiguration2 = worldDataConfiguration;
        } else {
            bl2 = true;
            worldDataConfiguration2 = new WorldDataConfiguration(DataPackConfig.DEFAULT, FeatureFlags.DEFAULT_FLAGS);
        }

        WorldLoader.PackConfig packConfig = new WorldLoader.PackConfig(packRepository, worldDataConfiguration2, bl, bl2);
        return new WorldLoader.InitConfig(packConfig, Commands.CommandSelection.DEDICATED, 2);
    }
}
