package net.shadew.debug.impl.menu;

import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.mutable.MutableBoolean;

import net.shadew.debug.api.menu.BooleanItem;
import net.shadew.debug.api.menu.OptionSelectContext;

public class Item extends BooleanItem {
    private final MutableBoolean value;

    public Item(Component name, MutableBoolean value) {
        super(name);
        this.value = value;
    }

    @Override
    protected void toggle(OptionSelectContext context) {
        value.setValue(!value.booleanValue());
    }

    @Override
    protected boolean get() {
        return value.booleanValue();
    }
}
