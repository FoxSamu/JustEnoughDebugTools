package net.shadew.debug.api;

import net.shadew.debug.api.status.ServerDebugStatus;

public interface DebugInitializer {
    void onInitializeDebug(ServerDebugStatus status);
}
