package net.shadew.debug.api.menu;

import net.minecraft.network.chat.Component;

/**
 * An option that opens a child menu, of type {@link OptionType#MENU}.
 *
 * @author Shadew
 * @see DebugOption
 * @see AbstractDebugOption
 * @since 0.1
 */
public class MenuOption extends AbstractDebugOption {
    private final DebugMenu menu;

    /**
     * @param name The name to display on the option widget
     * @param menu The menu to open
     * @since 0.1
     */
    public MenuOption(Component name, DebugMenu menu) {
        super(name);
        this.menu = menu;
    }

    /**
     * @param menu The menu to open, of which the header is displayed on the option widget
     * @since 0.1
     */
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
