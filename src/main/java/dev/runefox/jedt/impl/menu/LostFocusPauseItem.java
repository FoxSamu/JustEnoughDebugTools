package dev.runefox.jedt.impl.menu;

import dev.runefox.jedt.api.menu.BooleanItem;
import dev.runefox.jedt.api.menu.OptionSelectContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class LostFocusPauseItem extends BooleanItem {
    public LostFocusPauseItem(Component name) {
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
