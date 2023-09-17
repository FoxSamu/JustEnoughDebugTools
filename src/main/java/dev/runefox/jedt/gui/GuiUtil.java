package dev.runefox.jedt.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;


public class GuiUtil {
    public static Button button(int x, int y, int w, int h, Component text, Button.OnPress action) {
        return Button.builder(text, action)
                     .bounds(x, y, w, h)
                     .build();
    }
}
