package net.shadew.debug.api.menu;

import net.minecraft.resources.ResourceLocation;

public interface DebugMenuManager {
    default DebugMenu getMenu(String name) {
        return getMenu(new ResourceLocation(name));
    }

    DebugMenu getMenu(ResourceLocation name);
}
