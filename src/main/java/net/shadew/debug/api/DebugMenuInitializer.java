package net.shadew.debug.api;

import net.shadew.debug.api.menu.DebugMenu;
import net.shadew.debug.api.menu.DebugMenuManager;
import net.shadew.debug.api.status.ServerDebugStatus;

public interface DebugMenuInitializer {
    void onInitializeDebugMenu(DebugMenu root, DebugMenuManager factory, ServerDebugStatus debugStatus);
}
