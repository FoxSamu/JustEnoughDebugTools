package net.shadew.debug.api.menu;

/**
 * The types of debug menu options. This is returned by {@link DebugOption#getType()} to determine the appearance and
 * behavior of the option.
 *
 * @author Shadew
 * @see DebugOption#getType()
 * @since 0.1
 */
public enum OptionType {
    /**
     * Type for a basic button option. This renders the option as a plain button.
     *
     * @since 0.1
     */
    ACTION,

    /**
     * Type for an option with that opens a submenu. This renders the option as a button with a right-pointing arrow.
     *
     * @since 0.1
     */
    MENU,

    /**
     * Type for an option that can be toggled. This renders a check on the option if enabled.
     *
     * @since 0.1
     */
    BOOLEAN,

    /**
     * Type for an option with a numeric or spinnable value. This renders two arrow buttons on the option.
     *
     * @since 0.1
     */
    NUMBER
}
