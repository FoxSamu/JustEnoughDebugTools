package dev.runefox.jedt.impl.menu;

import dev.runefox.jedt.api.menu.BooleanItem;
import dev.runefox.jedt.api.menu.OptionSelectContext;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.mutable.MutableBoolean;

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
