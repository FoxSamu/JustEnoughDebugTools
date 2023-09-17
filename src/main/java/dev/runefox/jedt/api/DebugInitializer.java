package dev.runefox.jedt.api;

import dev.runefox.jedt.api.status.ServerDebugStatus;

public interface DebugInitializer {
    void onInitializeDebug(ServerDebugStatus status);
}
