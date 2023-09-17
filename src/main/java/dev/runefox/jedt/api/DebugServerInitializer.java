package dev.runefox.jedt.api;

import dev.runefox.jedt.api.status.ServerDebugStatus;

public interface DebugServerInitializer {
    void onInitializeDebugServer(ServerDebugStatus status);
}
