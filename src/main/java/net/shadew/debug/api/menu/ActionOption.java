package net.shadew.debug.api.menu;

import net.minecraft.network.chat.Component;

public abstract class ActionOption extends AbstractDebugOption {
    public ActionOption(Component name) {
        super(name);
    }

    @Override
    public final OptionType getType() {
        return OptionType.ACTION;
    }
}
