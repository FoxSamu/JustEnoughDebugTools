package net.shadew.debug.impl.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import net.shadew.debug.api.menu.BooleanOption;
import net.shadew.debug.api.menu.OptionSelectContext;

public class LostFocusPauseOption extends BooleanOption {
    public LostFocusPauseOption(Component name) {
        super(name);
    }

    @Override
    protected void toggle(OptionSelectContext context) {
        Minecraft client = Minecraft.getInstance();
        client.options.pauseOnLostFocus = !client.options.pauseOnLostFocus;
        client.options.save();
    }

    @Override
    protected boolean get() {
        Minecraft client = Minecraft.getInstance();
        return client.options.pauseOnLostFocus;
    }
}
