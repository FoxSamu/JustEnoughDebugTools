package net.shadew.debug.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Lifecycle;
import net.minecraft.CrashReport;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.shadew.debug.Debug;
import net.shadew.debug.api.gametest.GameTestCIUtil;
import net.shadew.debug.api.gametest.GameTestEvents;

// Patch of GameTestServer, so it can be patched to add more JEDT
public class DebugGameTestServer extends MinecraftServer {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int PROGRESS_REPORT_INTERVAL = 20;

    private static final GameRules TEST_GAME_RULES = Util.make(new GameRules(), rules -> {
        rules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, null);
        rules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, null);
    });
    private static final LevelSettings TEST_SETTINGS = new LevelSettings(
        "Test Level",           // Name
        GameType.CREATIVE,      // Gamemode
        false,                  // Hardcore
        Difficulty.NORMAL,      // Difficulty
        true,                   // Commands
        TEST_GAME_RULES,        // Game rules
        DataPackConfig.DEFAULT  // Data packs
    );

    private final List<GameTestBatch> testBatches;
    private final RuntimeTestConfig config;
    private final File serverDir;

    private MultipleTestTracker testTracker;

    public DebugGameTestServer(Thread mainThread, File serverDir, LevelStorageSource.LevelStorageAccess storage, PackRepository packRepo, ServerResources resources, RuntimeTestConfig config, RegistryAccess.RegistryHolder registries) {
        this(
            mainThread, serverDir, storage, packRepo, resources, config, registries,
            registries.registryOrThrow(Registry.BIOME_REGISTRY),
            registries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY)
        );
    }

    private DebugGameTestServer(Thread mainThread, File serverDir, LevelStorageSource.LevelStorageAccess storage, PackRepository packRepo, ServerResources resources, RuntimeTestConfig config, RegistryAccess.RegistryHolder registries, Registry<Biome> biomes, Registry<DimensionType> dimensions) {
        super(
            mainThread, registries, storage,
            new PrimaryLevelData(
                TEST_SETTINGS,
                new WorldGenSettings(
                    0,     // Seed
                    false, // Features
                    false, // Bonus chest
                    WorldGenSettings.withOverworld(
                        dimensions,
                        DimensionType.defaultDimensions(
                            dimensions, biomes,
                            registries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY),
                            0
                        ),
                        new FlatLevelSource(FlatLevelGeneratorSettings.getDefault(biomes))
                    )
                ),
                Lifecycle.stable()
            ),
            packRepo, Proxy.NO_PROXY, DataFixers.getDataFixer(), resources,
            null, null, null, // Auth stuff, can be null since nobody will join anyway
            LoggerChunkProgressListener::new
        );

        Collection<GameTestBatch> batches = groupTestsIntoBatches(config.getFilteredTests(), config.getMaxSimultaneous());
        this.config = config;
        this.testBatches = Lists.newArrayList(batches);
        this.serverDir = serverDir;

        if (batches.isEmpty()) {
            throw new IllegalArgumentException("No test batches were given!");
        }
    }

    private static Collection<GameTestBatch> groupTestsIntoBatches(Stream<TestFunction> tests, int partitionSize) {
        Map<String, List<TestFunction>> byBatchName = tests.collect(Collectors.groupingBy(TestFunction::getBatchName));

        return byBatchName.entrySet().stream().flatMap(entry -> {
            String string = entry.getKey();

            Consumer<ServerLevel> before = GameTestRegistry.getBeforeBatchFunction(string);
            Consumer<ServerLevel> after = GameTestRegistry.getAfterBatchFunction(string);
            MutableInt batchNr = new MutableInt();
            Collection<TestFunction> batchTests = entry.getValue();

            return Streams.stream(Iterables.partition(batchTests, partitionSize))
                          .map(partition -> new GameTestBatch(
                              string + ":" + batchNr.incrementAndGet(),
                              ImmutableList.copyOf(partition),
                              before, after
                          ));
        }).collect(ImmutableList.toImmutableList());
    }

    @Override
    public boolean initServer() {
        setPlayerList(new PlayerList(this, registryHolder, playerDataStorage, 1) {
        });
        loadLevel();
        ServerLevel overworld = overworld();
        overworld.setDefaultSpawnPos(config.getStart(), 0);
        overworld.getLevelData().setRaining(false);
        return true;
    }

    @Override
    public void tickServer(BooleanSupplier haveTime) {
        super.tickServer(haveTime);

        ServerLevel overworld = overworld();
        if (!haveTestsStarted())
            startTests(overworld);

        if (overworld.getGameTime() % PROGRESS_REPORT_INTERVAL == 0) {
            LOGGER.info(testTracker.getProgressBar());
        }

        if (testTracker.isDone()) {
            halt(false);
            LOGGER.info(testTracker.getProgressBar());
            GlobalTestReporter.finish();

            String header = String.format("========= %d GAME TESTS COMPLETE ======================", testTracker.getTotalCount());
            LOGGER.info(header);
            if (testTracker.hasFailedRequired()) {
                LOGGER.info("{} required tests failed {}", testTracker.getFailedRequiredCount(), Debug.UWU ? "qwq" : ":(");
                testTracker.getFailedRequired().forEach(
                    test -> LOGGER.info("   - {}", test.getTestName())
                );
            } else {
                LOGGER.info("All {} required tests passed {}", testTracker.getTotalCount(), Debug.UWU ? "^w^" : ":)");
            }

            if (testTracker.hasFailedOptional()) {
                LOGGER.info("{} optional tests failed {}", testTracker.getFailedOptionalCount(), Debug.UWU ? ">w<" : ":|");
                testTracker.getFailedOptional().forEach(
                    test -> LOGGER.info("   - {}", test.getTestName())
                );
            }

            LOGGER.info("=".repeat(header.length()));
        }

    }

    @Override
    public SystemReport fillServerSystemReport(SystemReport systemReport) {
        systemReport.setDetail("Type", "Game test server");
        return systemReport;
    }

    @Override
    public void onServerExit() {
        super.onServerExit();

        getRunningThread().interrupt();
        GameTestEvents.TEST_SERVER_DONE.invoker().onTestServerDone(this);

        config.getExportPath(serverDir.toPath()).ifPresent(path -> {
            LOGGER.info("Exporting test world as zip to " + path);
            GameTestCIUtil.exportTestWorldAsZip(this, path.toFile());
        });

        // Halt runtime here, prevent the server from hanging, which it somehow does
        Runtime.getRuntime().halt(testTracker.getFailedRequiredCount());
    }

    @Override
    public void onServerCrash(CrashReport crashReport) {
        config.getExportPath(serverDir.toPath()).ifPresent(path -> {
            LOGGER.info("Exporting test world as zip to " + path);
            GameTestCIUtil.exportTestWorldAsZip(this, path.toFile());
        });

        // Halt runtime here, prevent the server from hanging, which it somehow does
        Runtime.getRuntime().halt(1);
    }

    private void startTests(ServerLevel level) {
        Collection<GameTestInfo> tests = GameTestRunner.runTestBatches(
            testBatches, config.getStart(), Rotation.NONE, level,
            GameTestTicker.SINGLETON, 8
        );
        testTracker = new MultipleTestTracker(tests);
        LOGGER.info("{} tests are now running!", testTracker.getTotalCount());
    }

    private boolean haveTestsStarted() {
        return testTracker != null;
    }

    @Override
    public boolean isHardcore() {
        return false;
    }

    @Override
    public int getOperatorUserPermissionLevel() {
        return 0;
    }

    @Override
    public int getFunctionCompilationLevel() {
        return 4;
    }

    @Override
    public boolean shouldRconBroadcast() {
        return false;
    }

    @Override
    public boolean isDedicatedServer() {
        return false;
    }

    @Override
    public int getRateLimitPacketsPerSecond() {
        return 0;
    }

    @Override
    public boolean isEpollEnabled() {
        return false;
    }

    @Override
    public boolean isCommandBlockEnabled() {
        return true;
    }

    @Override
    public boolean isPublished() {
        return false;
    }

    @Override
    public boolean shouldInformAdmins() {
        return false;
    }

    @Override
    public boolean isSingleplayerOwner(GameProfile gameProfile) {
        return false;
    }

    @Override
    public Optional<String> getModdedStatus() {
        return Optional.empty();
    }
}
