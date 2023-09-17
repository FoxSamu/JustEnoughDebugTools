package dev.runefox.jedt.api.status;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;

public interface DebugStatusEvents {
    Event<Ready> READY = EventFactory.createArrayBacked(Ready.class, callbacks -> (server, status) -> {
        for (Ready ready : callbacks) {
            ready.ready(server, status);
        }
    });

    Event<ClientReady> CLIENT_READY = EventFactory.createArrayBacked(ClientReady.class, callbacks -> status -> {
        for (ClientReady ready : callbacks) {
            ready.ready(status);
        }
    });

    interface Ready {
        void ready(MinecraftServer server, ServerDebugStatus.Mutable status);
    }

    interface ClientReady {
        void ready(ServerDebugStatus status);
    }
}
