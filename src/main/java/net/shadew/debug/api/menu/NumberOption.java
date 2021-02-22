package net.shadew.debug.api.menu;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public abstract class NumberOption extends AbstractDebugOption {
    protected static final Text DEFAULT_NUMBER = new TranslatableText("debug.options.debug.default_number");

    public NumberOption(Text name) {
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
    public Text getDisplayValue() {
        return new TranslatableText("debug.options.debug.default_number", getValue())
                   .formatted(Formatting.YELLOW);
    }


}
