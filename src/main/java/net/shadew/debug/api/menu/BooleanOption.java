package net.shadew.debug.api.menu;

import net.minecraft.text.Text;

public abstract class BooleanOption extends AbstractDebugOption {
    public BooleanOption(Text name) {
        super(name);
    }

    @Override
    public final OptionType getType() {
        return OptionType.BOOLEAN;
    }

    @Override
    public void onClick(OptionSelectContext context) {
        toggle(context);
    }

    protected abstract void toggle(OptionSelectContext context);
    protected abstract boolean get();

    @Override
    public boolean hasCheck() {
        return get();
    }
}
