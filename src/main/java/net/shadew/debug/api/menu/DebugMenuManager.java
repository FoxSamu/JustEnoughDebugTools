package net.shadew.debug.api.menu;

import net.minecraft.util.Identifier;

public interface DebugMenuManager {
    default DebugMenu getMenu(String name) {
        return getMenu(new Identifier(name));
    }

    DebugMenu getMenu(Identifier name);
}
