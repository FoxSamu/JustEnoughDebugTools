package net.shadew.debug;

import com.google.gson.JsonParser;
import com.sun.media.jfxmedia.events.PlayerStateEvent;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.entrypoint.minecraft.hooks.EntrypointUtils;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.shadew.debug.api.DebugServerInitializer;
import net.shadew.debug.util.DebugNetwork;

public class DebugServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        EntrypointUtils.invoke(
            "debug:server", DebugServerInitializer.class,
            init -> init.onInitializeDebugServer(Debug.serverDebugStatus)
        );

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
            if (success) {
                reloadStatus(server);
            }
        });
    }

    private static void reloadStatus(MinecraftServer server) {
        File statusFile = server.getFile("debug_config.json");
        if (!statusFile.exists()) {
            Debug.LOGGER.info("debug_config.json not found");
            Debug.serverDebugStatus.setDebugAvailable(false);
        } else {
            try {
                Debug.serverDebugStatus.read(new JsonParser().parse(new FileReader(statusFile)).getAsJsonObject());
                Debug.LOGGER.info("Loaded debug_config.json");
            } catch (Exception exc) {
                Debug.LOGGER.error("Failed to load debug_config.json", exc);
            }
        }

        DebugNetwork.sendServerStatus(Debug.serverDebugStatus, server);
        Debug.serverDebugStatus.log(Debug.LOGGER);
    }
}
