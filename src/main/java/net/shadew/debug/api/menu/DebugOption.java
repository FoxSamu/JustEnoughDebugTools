package net.shadew.debug.api.menu;

import net.minecraft.text.Text;

public interface DebugOption {
    Text getName();

    default Text getLongName() {
        return getName();
    }

    OptionType getType();
    void onClick(OptionSelectContext context);

    default Text getDescription() {
        return null;
    }

    default boolean hasCheck() {
        return false;
    }

    default Text getDisplayValue() {
        return null;
    }

    default boolean isVisible() {
        return true;
    }
}
