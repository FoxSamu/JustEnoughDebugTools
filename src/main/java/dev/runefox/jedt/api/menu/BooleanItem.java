package dev.runefox.jedt.api.menu;

import net.minecraft.network.chat.Component;

/**
 * An abstract toggleable option, of type {@link OptionType#BOOLEAN}. A boolean option is managed by two methods:
 * {@link #get} and {@link #toggle}.
 *
 * @author Samū
 * @see Item
 * @see AbstractItem
 * @since 0.1
 */
public abstract class BooleanItem extends AbstractItem {
    public BooleanItem(Component name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link OptionType#BOOLEAN}
     *
     * @since 0.1
     */
    @Override
    public final OptionType getType() {
        return OptionType.BOOLEAN;
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.1
     */
    @Override
    public void onClick(OptionSelectContext context) {
        toggle(context);
    }

    /**
     * Toggles the configuration this option is supposed to manage.
     *
     * @param context The selection context.
     * @since 0.1
     */
    protected abstract void toggle(OptionSelectContext context);

    /**
     * Returns the current value of the configuration this option is supposed to manage.
     *
     * @since 0.1
     */
    protected abstract boolean get();

    /**
     * {@inheritDoc}
     *
     * @since 0.1
     */
    @Override
    public boolean hasCheck() {
        return get();
    }
}
