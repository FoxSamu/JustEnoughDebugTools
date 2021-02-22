package net.shadew.debug.impl.menu;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.Map;

import net.shadew.debug.api.menu.DebugMenu;
import net.shadew.debug.api.menu.DebugMenuManager;

public class DebugMenuManagerImpl implements DebugMenuManager {
    private final HashMap<Identifier, DebugMenu> menuInstances = new HashMap<>();

    @Override
    public DebugMenu getMenu(Identifier name) {
        if (name == null) {
            throw new NullPointerException();
        }
        return menuInstances.computeIfAbsent(name, this::createMenu);
    }

    public Map<Identifier, DebugMenu> getAllMenus() {
        return menuInstances;
    }

    private DebugMenu createMenu(Identifier name) {
        return new DebugMenuImpl(new TranslatableText(Util.createTranslationKey("debug.menu", name)));
    }
}
