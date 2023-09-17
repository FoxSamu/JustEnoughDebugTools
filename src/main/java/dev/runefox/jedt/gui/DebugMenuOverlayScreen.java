package dev.runefox.jedt.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class DebugMenuOverlayScreen extends Screen {

    private final Screen parentScreen;

    protected DebugMenuOverlayScreen(Component component, Screen parentScreen) {
        super(component);
        this.parentScreen = parentScreen;
    }

    public Screen getParentScreen() {
        return parentScreen;
    }

    @Override
    public final void onClose() {
        assert minecraft != null;
        minecraft.setScreen(parentScreen);
        onClosed();
    }

    protected void onClosed() {
    }
}
