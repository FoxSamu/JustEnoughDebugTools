package net.shadew.debug.api;

import net.shadew.debug.api.status.ServerDebugStatus;

public interface DebugServerInitializer {
    void onInitializeDebugServer(ServerDebugStatus status);
}
