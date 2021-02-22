package net.shadew.debug.api.menu;

import net.minecraft.text.Text;

public abstract class ActionOption extends AbstractDebugOption {
    public ActionOption(Text name) {
        super(name);
    }

    @Override
    public final OptionType getType() {
        return OptionType.ACTION;
    }
}
