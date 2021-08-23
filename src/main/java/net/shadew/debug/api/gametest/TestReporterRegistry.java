package net.shadew.debug.api.gametest;

import com.google.gson.JsonElement;
import net.minecraft.gametest.framework.LogTestReporter;
import net.minecraft.gametest.framework.TeamcityTestReporter;
import net.minecraft.gametest.framework.TestReporter;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.shadew.debug.test.ProperJUnitLikeTestReporter;

class TestReporterRegistry {
    static final Map<ResourceLocation, TestReporterType> REG = new HashMap<>();

    static {
        REG.put(new ResourceLocation("jedt:log"), new Log());
        REG.put(new ResourceLocation("jedt:junit"), new JUnit());
        REG.put(new ResourceLocation("jedt:teamcity"), new Teamcity());
        REG.put(new ResourceLocation("log"), new Log());
        REG.put(new ResourceLocation("junit"), new JUnit());
        REG.put(new ResourceLocation("teamcity"), new Teamcity());
    }

    private static class Log implements TestReporterType {
        @Override
        public TestReporter setup(JsonElement config, File serverDir) {
            return new LogTestReporter();
        }
    }

    private static class JUnit implements TestReporterType {
        @Override
        public TestReporter setup(JsonElement config, File serverDir) throws Exception {
            if (config == null) {
                throw new RuntimeException("No export file for junit reporter");
            }
            String path = config.getAsString();
            if (path.startsWith("[") && path.endsWith("]")) {
                path = System.getProperty(path.substring(1, path.length() - 1));
                if (path == null) {
                    throw new RuntimeException("No export file for junit reporter");
                }
            }
            return new ProperJUnitLikeTestReporter(serverDir.toPath().resolve(config.getAsString()).toFile());
        }
    }

    private static class Teamcity implements TestReporterType {
        @Override
        public TestReporter setup(JsonElement config, File serverDir) {
            return new TeamcityTestReporter();
        }
    }
}
