package net.shadew.debug.impl.menu;

import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

import net.shadew.debug.api.menu.DebugMenu;
import net.shadew.debug.api.menu.DebugMenuManager;

public class DebugMenuManagerImpl implements DebugMenuManager {
    public static final ResourceLocation ROOT = new ResourceLocation("debug:root");

    private final HashMap<ResourceLocation, DebugMenuImpl> menuInstances = new HashMap<>();

    @Override
    public DebugMenu getMenu(ResourceLocation name) {
        if (name == null) {
            name = ROOT;
        }
        return menuInstances.computeIfAbsent(name, this::createMenu);
    }

    public void clearAll() {
        menuInstances.forEach((key, menu) -> menu.clear());
    }

    public Map<ResourceLocation, DebugMenuImpl> getAllMenus() {
        return menuInstances;
    }

    private DebugMenuImpl createMenu(ResourceLocation name) {
        return new DebugMenuImpl(new TranslatableComponent(Util.makeDescriptionId("debug.menu", name)));
    }
}
