package dev.runefox.jedt.api;

import dev.runefox.jedt.api.status.ServerDebugStatus;

public interface DebugStatusInitializer {
    void onInitializeDebugStatus(ServerDebugStatus.Builder statusBuilder);
}
