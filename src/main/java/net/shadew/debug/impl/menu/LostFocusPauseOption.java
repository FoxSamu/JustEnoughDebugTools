package net.shadew.debug.impl.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import net.shadew.debug.api.menu.BooleanOption;
import net.shadew.debug.api.menu.OptionSelectContext;

public class LostFocusPauseOption extends BooleanOption {
    public LostFocusPauseOption(Text name) {
        super(name);
    }

    @Override
    protected void toggle(OptionSelectContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.options.pauseOnLostFocus = !client.options.pauseOnLostFocus;
        client.options.write();
    }

    @Override
    protected boolean get() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.options.pauseOnLostFocus;
    }
}
