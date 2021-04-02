package net.shadew.debug.api.menu;

import net.minecraft.network.chat.Component;

public class MenuOption extends AbstractDebugOption {
    private final DebugMenu menu;

    public MenuOption(Component name, DebugMenu menu) {
        super(name);
        this.menu = menu;
    }

    public MenuOption(DebugMenu menu) {
        this(menu.getHeader(), menu);
    }

    @Override
    public final OptionType getType() {
        return OptionType.MENU;
    }

    @Override
    public void onClick(OptionSelectContext context) {
        context.openMenu(menu);
    }
}
