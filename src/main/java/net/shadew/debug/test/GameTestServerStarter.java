package net.shadew.debug.test;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.entrypoint.minecraft.hooks.EntrypointServer;
import net.minecraft.CrashReport;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.GlobalTestReporter;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import net.shadew.debug.api.GameTestInitializer;
import net.shadew.debug.util.PathUtil;

public class GameTestServerStarter {
    private static final Logger LOGGER = LogManager.getLogger();

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
            File configPath = PathUtil.resolve(universe.toPath(), config).toFile();

            if (!configPath.exists()) {
                LOGGER.error("Could not find test config file at {}", configPath);
                return;
            }

            LOGGER.info("Loading test server configuration from {}", configPath);

            RuntimeTestConfig rtConfig = GameTestIntegration.loadRuntimeTestConfig(configPath);
            if (rtConfig == null) {
                // GLaDOS is sad now
                LOGGER.error("Failed to load test config file at {}, cannot continue", configPath);
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

            RegistryAccess.RegistryHolder registries = RegistryAccess.builtin();

            /*
             * Setup level
             */
            String levelName = Optional.ofNullable(options.valueOf(worldSpec)).orElse("gametestworld");
            LevelStorageSource storageSrc = LevelStorageSource.createDefault(universe.toPath());
            LevelStorageSource.LevelStorageAccess storageAcc = storageSrc.createAccess(levelName);
            MinecraftServer.convertFromRegionFormatIfNeeded(storageAcc);

            LevelSummary summary = storageAcc.getSummary();
            if (summary != null && summary.isIncompatibleWorldHeight()) {
                LOGGER.info("Loading of old worlds is temporarily disabled.");
                return;
            }

            /*
             * Initiate mods
             */

            // This is the moment we start to load data packs, we must now load mods.
            // We don't have a game instance yet, we set this at the end.
            EntrypointServer.start(null, null);

            // Only call the game test entrypoints that are part of the selected mod
            List<EntrypointContainer<GameTestInitializer>> entrypointContainers
                = FabricLoader.INSTANCE.getEntrypointContainers("debug:gametest", GameTestInitializer.class);

            for (EntrypointContainer<GameTestInitializer> ep : entrypointContainers) {
                if (rtConfig.getModConfig(ep.getProvider().getMetadata().getId()) != null) {
                    ep.getEntrypoint().initializeGameTestServer();
                }
            }

            /*
             * Load datapacks
             */
            Optional<Path> dataPacksPath = rtConfig.getDatapacksPath(universe.toPath());

            DataPackConfig dataPacks = storageAcc.getDataPacks();
            PackRepository packRepository = dataPacksPath.map(
                path -> new PackRepository(
                    PackType.SERVER_DATA,
                    new ServerPacksSource(),
                    new FolderRepositorySource(path.toFile(), PackSource.SERVER),
                    new FolderRepositorySource(storageAcc.getLevelPath(LevelResource.DATAPACK_DIR).toFile(), PackSource.WORLD)
                )
            ).orElseGet(
                () -> new PackRepository(
                    PackType.SERVER_DATA,
                    new ServerPacksSource(),
                    new FolderRepositorySource(storageAcc.getLevelPath(LevelResource.DATAPACK_DIR).toFile(), PackSource.WORLD)
                )
            );
            MinecraftServer.configurePackRepository(
                packRepository,
                dataPacks == null ? DataPackConfig.DEFAULT : dataPacks,
                false // Clean datapacks
            );
            CompletableFuture<ServerResources> resourcesFuture = ServerResources.loadResources(
                packRepository.openAllSelected(),
                registries,
                Commands.CommandSelection.DEDICATED,
                2, // Function permission level
                Util.backgroundExecutor(),
                Runnable::run
            );

            ServerResources resources;
            try {
                resources = resourcesFuture.get();
            } catch (Exception exc) {
                LOGGER.warn("Failed to load datapacks, can't proceed with test server load", exc);
                packRepository.close();
                return;
            }

            resources.updateGlobals();

            /*
             * Setup GameTest
             */

            // This is needed for the server to run the tests
            SharedConstants.IS_RUNNING_IN_IDE = true;
            Path serverDir = universe.toPath();

            // Set test structures directory
            rtConfig.getTestStructuresPath(serverDir)
                    .ifPresent(path -> StructureUtils.testStructuresDir = path.toString());

            // Initiate TestReporter
            GlobalTestReporter.replaceWith(rtConfig.instantiateReporter(serverDir));

            // Register methods
            String[] sets = rtConfig.getAllModSets()
                                    .filter(rtConfig::includesSet)
                                    .distinct()
                                    .toArray(String[]::new);
            rtConfig.getAllTestMethods(sets)
                    .forEach(GameTestRegistry::register);

            /*
             * Start the server
             */
            LOGGER.info("Starting JEDT game test server");

            DebugGameTestServer server = MinecraftServer.spin(thread -> {
                DebugGameTestServer serverInst = new DebugGameTestServer(
                    thread,
                    universe,
                    storageAcc,
                    packRepository,
                    resources,
                    rtConfig,
                    registries
                );

                return serverInst;
            });

            // Set the game instance, we must do this here since we had no game instance at modloading time
            // Yes this deprecated and not to be called anywhere else than from the dedicated server ...
            //
            // ... but we are the dedicated server now :D
            FabricLoader.INSTANCE.setGameInstance(server);

            Thread shutdownThread = new Thread(() -> server.halt(true));
            shutdownThread.setName("Server Shutdown Thread");
            shutdownThread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
            Runtime.getRuntime().addShutdownHook(shutdownThread);
        } catch (Exception exc) {
            LOGGER.fatal("Failed to start the test server", exc);
        }
    }
}
