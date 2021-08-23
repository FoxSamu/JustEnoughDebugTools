package net.shadew.debug.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.LogTestReporter;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.gametest.framework.TestReporter;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.shadew.debug.api.gametest.TestReporterType;
import net.shadew.debug.util.PathUtil;

public class RuntimeTestConfig {
    private final Set<String> sets = new HashSet<>();
    private final Set<String> mods = new HashSet<>();
    private final List<Predicate<TestFunction>> filters = new ArrayList<>();
    private int startX = 0, startY = 4, startZ = 0;
    private String exportWorldPath;
    private String testStructuresPath;
    private String dimension = "overworld";
    private String datapacksPath;
    private JsonElement reporter;
    private int maxSimultaneous = 100;
    private final Map<String, ModTestConfig> modConfigs = new HashMap<>();

    public void addSet(String set) {
        sets.add(set);
    }

    public Set<String> getSets() {
        return sets;
    }

    public boolean includesSet(String set) {
        return sets.isEmpty() // All sets
                   || sets.contains("*") && !sets.contains("!" + set) // All mods, except specific sets
                   || sets.contains(set); // Specific list of sets
    }

    public void addMod(String mod) {
        mods.add(mod);
    }

    public Set<String> getMods() {
        return mods;
    }

    public boolean includesMod(String mod) {
        return mods.isEmpty() // All mods
                   || mods.contains("*") && !mods.contains("!" + mod) // All mods, except specific mods
                   || mods.contains(mod); // Specific list of mods
    }

    public void addFilter(Predicate<TestFunction> filter) {
        filters.add(filter);
    }

    public List<Predicate<TestFunction>> getFilters() {
        return filters;
    }

    public Stream<TestFunction> getFilteredTests() {
        Stream<TestFunction> stream = GameTestRegistry.getAllTestFunctions().stream();
        filters.forEach(stream::filter);
        return stream;
    }

    public void setStart(int x, int z) {
        startX = x;
        startZ = z;
    }

    public void setStart(int x, int y, int z) {
        startX = x;
        startY = y;
        startZ = z;
    }

    public BlockPos getStart() {
        return new BlockPos(startX, startY, startZ);
    }

    public void setExportPath(String path) {
        exportWorldPath = path;
    }

    public Optional<Path> getExportPath(Path serverDir) {
        if (exportWorldPath == null) return Optional.empty();
        return Optional.of(PathUtil.resolve(serverDir, exportWorldPath));
    }

    public void setTestStructuresPath(String testStructuresPath) {
        this.testStructuresPath = testStructuresPath;
    }

    public Optional<Path> getTestStructuresPath(Path serverDir) {
        if (testStructuresPath == null) return Optional.empty();
        return Optional.of(PathUtil.resolve(serverDir, testStructuresPath));
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDatapacksPath(String datapacksPath) {
        this.datapacksPath = datapacksPath;
    }

    public Optional<Path> getDatapacksPath(Path serverDir) {
        if (datapacksPath == null) return Optional.empty();
        return Optional.of(PathUtil.resolve(serverDir, datapacksPath));
    }

    public void setReporter(JsonElement reporter) {
        this.reporter = reporter;
    }

    public TestReporter instantiateReporter(Path serverDir) throws Exception {
        if (reporter == null) {
            return new LogTestReporter();
        }
        File serverDirF = serverDir.toFile();
        if (reporter.isJsonArray()) {
            MultiTestReporter rep = new MultiTestReporter();
            for (JsonElement el : reporter.getAsJsonArray()) {
                rep.addReporter(reporter(el, serverDirF));
            }
            return rep;
        } else {
            return reporter(reporter, serverDirF);
        }
    }

    private static TestReporter reporter(JsonElement config, File serverDir) throws Exception {
        if (config.isJsonPrimitive()) {
            return TestReporterType.instantiate(new ResourceLocation(config.getAsString()), null, serverDir);
        } else {
            JsonObject obj = config.getAsJsonObject();
            if (obj.size() != 1) {
                throw new Exception("Failed to configure reporter from object with incorrect size");
            }
            Map.Entry<String, JsonElement> entry = obj.entrySet().iterator().next();
            return TestReporterType.instantiate(new ResourceLocation(entry.getKey()), entry.getValue(), serverDir);
        }
    }

    public void setMaxSimultaneous(int maxSimultaneous) {
        this.maxSimultaneous = maxSimultaneous;
    }

    public int getMaxSimultaneous() {
        return maxSimultaneous;
    }

    public Map<String, ModTestConfig> getModConfigsById() {
        return modConfigs;
    }

    public Collection<ModTestConfig> getModConfigs() {
        return modConfigs.values();
    }

    public ModTestConfig getModConfig(String modId) {
        return modConfigs.get(modId);
    }

    public void addModConfig(ModTestConfig config) {
        modConfigs.put(config.getModId(), config);
    }

    public Stream<Method> getAllTestMethods(String... sets) {
        return modConfigs.values().stream().flatMap(config -> config.getMethods(sets));
    }

    public Stream<String> getAllModSets() {
        return modConfigs.values().stream().flatMap(ModTestConfig::getSets);
    }
}
