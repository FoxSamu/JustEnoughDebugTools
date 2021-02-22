package net.shadew.debug.api.menu;

import net.minecraft.text.Text;

import java.util.function.Consumer;

public class SimpleActionOption extends ActionOption {
    private final Consumer<OptionSelectContext> handler;
    public SimpleActionOption(Text name, Consumer<OptionSelectContext> handler) {
        super(name);
        this.handler = handler;
    }

    @Override
    public void onClick(OptionSelectContext context) {
        handler.accept(context);
    }
}
