package net.shadew.debug.impl.menu;

import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

import net.shadew.debug.api.menu.DebugMenu;
import net.shadew.debug.api.menu.DebugMenuManager;

public class DebugMenuManagerImpl implements DebugMenuManager {
    private final HashMap<ResourceLocation, DebugMenu> menuInstances = new HashMap<>();

    @Override
    public DebugMenu getMenu(ResourceLocation name) {
        if (name == null) {
            throw new NullPointerException();
        }
        return menuInstances.computeIfAbsent(name, this::createMenu);
    }

    public Map<ResourceLocation, DebugMenu> getAllMenus() {
        return menuInstances;
    }

    private DebugMenu createMenu(ResourceLocation name) {
        return new DebugMenuImpl(new TranslatableComponent(Util.makeDescriptionId("debug.menu", name)));
    }
}
