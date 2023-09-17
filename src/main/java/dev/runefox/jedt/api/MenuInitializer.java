package dev.runefox.jedt.api;

import dev.runefox.jedt.api.menu.Menu;
import dev.runefox.jedt.api.menu.MenuManager;
import dev.runefox.jedt.api.status.ServerDebugStatus;

public interface MenuInitializer {
    void onInitializeDebugMenu(Menu root, MenuManager factory, ServerDebugStatus debugStatus);
}
