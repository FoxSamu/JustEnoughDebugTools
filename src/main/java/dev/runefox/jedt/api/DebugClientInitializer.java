package dev.runefox.jedt.api;

import dev.runefox.jedt.api.status.ServerDebugStatus;

public interface DebugClientInitializer {
    void onInitializeDebugClient(ServerDebugStatus statusInstance);
}
