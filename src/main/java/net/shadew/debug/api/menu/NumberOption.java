package net.shadew.debug.api.menu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class NumberOption extends AbstractDebugOption {
    protected static final String DEFAULT_NUMBER_TK = "debug.options.debug.default_number";

    public NumberOption(Component name) {
        super(name);
    }

    @Override
    public final OptionType getType() {
        return OptionType.NUMBER;
    }

    @Override
    public void onClick(OptionSelectContext context) {
        mutateValue(context.delta(), context);
    }

    protected abstract int getValue();
    protected abstract void mutateValue(int delta, OptionSelectContext context);

    @Override
    public Component getDisplayValue() {
        return new TranslatableComponent(DEFAULT_NUMBER_TK, getValue()).withStyle(ChatFormatting.YELLOW);
    }
}
