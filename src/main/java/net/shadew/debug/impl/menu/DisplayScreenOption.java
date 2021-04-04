package net.shadew.debug.impl.menu;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BiFunction;

import net.shadew.debug.api.menu.ExternalOption;
import net.shadew.debug.api.menu.OptionSelectContext;

public class DisplayScreenOption extends ExternalOption {
    private final BiFunction<Component, Screen, Screen> screenFactory;
    private boolean closeScreenAfterwards;

    public DisplayScreenOption(Component name, BiFunction<Component, Screen, Screen> screenFactory) {
        super(name);
        this.screenFactory = screenFactory;
    }

    @Override
    public void onClick(OptionSelectContext context) {
//        if (closeScreenAfterwards)
//            context.closeScreen();
        context.openScreen(screenFactory.apply(getName(), closeScreenAfterwards ? null : context.debugMenuScreen()));
    }

    public DisplayScreenOption closeScreenOnClick() {
        closeScreenAfterwards = true;
        return this;
    }

    public DisplayScreenOption closeNothingOnClick() {
        closeScreenAfterwards = false;
        return this;
    }
}
