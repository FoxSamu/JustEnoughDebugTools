package dev.runefox.jedt.api.menu;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class SimpleActionItem extends ActionItem {
    private final Consumer<OptionSelectContext> handler;

    public SimpleActionItem(Component name, Consumer<OptionSelectContext> handler) {
        super(name);
        this.handler = handler;
    }

    @Override
    public void onClick(OptionSelectContext context) {
        handler.accept(context);
    }
}
