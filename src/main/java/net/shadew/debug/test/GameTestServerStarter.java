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
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTestBatch;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.gametest.framework.GameTestServer;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import net.shadew.debug.api.GameTestInitializer;

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
    public static void startServer(String[] strings, String mod) {
        OptionParser optionParser = new OptionParser();
        OptionSpec<Void> helpSpec = optionParser.accepts("help").forHelp();
        OptionSpec<String> worldSpec = optionParser.accepts("world").withRequiredArg();

        try {
            OptionSet options = optionParser.parse(strings);
            if (options.has(helpSpec)) {
                optionParser.printHelpOn(System.err);
                return;
            }

            // Find mod that was asked for testing
            Optional<ModContainer> optContainer = FabricLoader.INSTANCE.getModContainer(mod);
            if (optContainer.isEmpty()) {
                throw new RuntimeException("No mod container found with ID: " + mod);
            }

            ModContainer container = optContainer.get();

            // Pre-load
            CrashReport.preload();
            Bootstrap.bootStrap();
            Bootstrap.validate();
            //Util.startTimerHackThread();

            RegistryAccess.RegistryHolder registries = RegistryAccess.builtin();

            File universe = new File(".");

            // Level
            String levelName = Optional.ofNullable(options.valueOf(worldSpec)).orElse("gametestworld");
            LevelStorageSource storageSrc = LevelStorageSource.createDefault(universe.toPath());
            LevelStorageSource.LevelStorageAccess storageAcc = storageSrc.createAccess(levelName);
            MinecraftServer.convertFromRegionFormatIfNeeded(storageAcc);

            LevelSummary summary = storageAcc.getSummary();
            if (summary != null && summary.isIncompatibleWorldHeight()) {
                LOGGER.info("Loading of old worlds is temporarily disabled.");
                return;
            }

            // This is the moment we start to load data packs, we must now load mods.
            // We don't have a game instance yet, we set this at the end.
            EntrypointServer.start(null, null);

            // Only call the entrypoints that are part of the selected mod
            List<EntrypointContainer<GameTestInitializer>> entrypointContainers
                = FabricLoader.INSTANCE.getEntrypointContainers("debug:gametest", GameTestInitializer.class);

            for (EntrypointContainer<GameTestInitializer> ep : entrypointContainers) {
                if (ep.getProvider() == container) {
                    ep.getEntrypoint().initializeGameTestServer();
                }
            }

            // Load data packs
            DataPackConfig dataPacks = storageAcc.getDataPacks();
            PackRepository packRepository = new PackRepository(
                PackType.SERVER_DATA,
                new ServerPacksSource(),
                new FolderRepositorySource(storageAcc.getLevelPath(LevelResource.DATAPACK_DIR).toFile(), PackSource.WORLD)
            );
            DataPackConfig configuredDataPacks = MinecraftServer.configurePackRepository(packRepository, dataPacks == null ? DataPackConfig.DEFAULT : dataPacks, false);
            CompletableFuture<ServerResources> resourcesFuture = ServerResources.loadResources(packRepository.openAllSelected(), registries, Commands.CommandSelection.DEDICATED, 2 /* function permission level */, Util.backgroundExecutor(), Runnable::run);

            ServerResources resources;
            try {
                resources = resourcesFuture.get();
            } catch (Exception exc) {
                LOGGER.warn("Failed to load datapacks, can't proceed with server load", exc);
                packRepository.close();
                return;
            }

            resources.updateGlobals();

            // Just in case
            SharedConstants.IS_RUNNING_IN_IDE = true;

            Collection<GameTestBatch> batches = GameTestRunner.groupTestsIntoBatches(GameTestRegistry.getAllTestFunctions());

            GameTestServer server = MinecraftServer.spin(threadx -> {
                GameTestServer serverInst = new GameTestServer(
                    threadx,
                    storageAcc,
                    packRepository,
                    resources,
                    batches,
                    new BlockPos(0, 4, 0),
                    registries
                );

                return serverInst;
            });

            // Set the game instance, we must do this here since we had no game instance at modloading time
            // Yes this deprecated and not to be called anywhere else than from the dedicated server ...
            //
            // ... but we are the server now :)
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
