package net.shadew.debug.api;

import net.shadew.debug.api.status.ServerDebugStatus;

public interface DebugStatusInitializer {
    void onInitializeDebugStatus(ServerDebugStatus.Builder statusBuilder);
}
