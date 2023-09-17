package dev.runefox.jedt.api.menu;

import net.minecraft.network.chat.Component;

/**
 * An abstract clickable option, of type {@link OptionType#EXTERNAL}.
 *
 * @author Samū
 * @see Item
 * @see AbstractItem
 * @since 0.2
 */
public abstract class ExternalOption extends AbstractItem {

    /**
     * @param name The name to display on the option widget
     * @since 0.2
     */
    public ExternalOption(Component name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link OptionType#EXTERNAL}
     *
     * @since 0.2
     */
    @Override
    public final OptionType getType() {
        return OptionType.EXTERNAL;
    }
}
