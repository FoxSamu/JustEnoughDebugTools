package net.shadew.debug.api.menu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * An abstract numeric option, of type {@link OptionType#NUMBER}. A numeric option is managed by two methods: {@link
 * #get} and {@link #mutate}.
 *
 * @author Shadew
 * @see DebugOption
 * @see AbstractDebugOption
 * @since 0.1
 */
public abstract class NumberOption extends AbstractDebugOption {

    /**
     * @param name The name to display on the option widget
     * @since 0.1
     */
    public NumberOption(Component name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link OptionType#NUMBER}
     *
     * @since 0.1
     */
    @Override
    public final OptionType getType() {
        return OptionType.NUMBER;
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.1
     */
    @Override
    public void onClick(OptionSelectContext context) {
        mutate(context.delta(), context);
    }

    /**
     * Returns the current value of the configuration managed by this option.
     *
     * @since 0.1
     */
    protected abstract int get();

    /**
     * Changes the configuration managed by this option by the given delta.
     *
     * @param delta   The value to add to the configuration to properly mutate the value of the configuration.
     * @param context The selection context
     * @since 0.1
     */
    protected abstract void mutate(int delta, OptionSelectContext context);

    /**
     * {@inheritDoc}
     *
     * @since 0.1
     */
    @Override
    public Component getDisplayValue() {
        return new TranslatableComponent("debug.options.jedt.default_number", get()).withStyle(ChatFormatting.YELLOW);
    }
}
