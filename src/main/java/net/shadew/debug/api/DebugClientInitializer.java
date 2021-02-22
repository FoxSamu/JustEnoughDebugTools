package net.shadew.debug.api;

import net.shadew.debug.api.status.ServerDebugStatus;

public interface DebugClientInitializer {
    void onInitializeDebugClient(ServerDebugStatus statusInstance);
}
