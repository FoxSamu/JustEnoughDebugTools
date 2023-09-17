package net.shadew.debug.api;

import net.shadew.debug.api.menu.Menu;
import net.shadew.debug.api.menu.MenuManager;
import net.shadew.debug.api.status.ServerDebugStatus;

public interface MenuInitializer {
    void onInitializeDebugMenu(Menu root, MenuManager factory, ServerDebugStatus debugStatus);
}
