package net.shadew.debug.api.menu;

import net.minecraft.network.chat.Component;

/**
 * An abstract clickable option, of type {@link OptionType#ACTION}.
 *
 * @author Samū
 * @see Item
 * @see AbstractItem
 * @since 0.1
 */
public abstract class ActionItem extends AbstractItem {

    /**
     * @param name The name to display on the option widget
     * @since 0.1
     */
    public ActionItem(Component name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link OptionType#ACTION}
     *
     * @since 0.1
     */
    @Override
    public final OptionType getType() {
        return OptionType.ACTION;
    }
}
