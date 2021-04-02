package net.shadew.debug.api.menu;

import net.minecraft.network.chat.Component;

public interface DebugOption {
    Component getName();

    default Component getLongName() {
        return getName();
    }

    OptionType getType();
    void onClick(OptionSelectContext context);

    default Component getDescription() {
        return null;
    }

    default boolean hasCheck() {
        return false;
    }

    default Component getDisplayValue() {
        return null;
    }

    default boolean isVisible() {
        return true;
    }
}
