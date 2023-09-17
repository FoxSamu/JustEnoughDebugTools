package dev.runefox.jedt.api.menu;

import dev.runefox.jedt.api.MenuInitializer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.stream.Stream;

/**
 * A debug menu to show in the debug tools menu. Debug menus can be initialized via a {@link MenuManager} instance.
 *
 * @author Samū
 * @see MenuManager
 * @see Item
 * @see MenuInitializer
 * @since 0.1
 */
public interface Menu {
    /**
     * The identifier of the root menu: the first menu that appears when opening the debug menu screen.
     */
    ResourceLocation ROOT = new ResourceLocation("jedt:root");

    /**
     * The identifier of the 'Quick Commands' menu.
     */
    ResourceLocation COMMANDS = new ResourceLocation("jedt:commands");

    /**
     * The identifier of the 'Time' menu in the 'Quick Commands' menu.
     */
    ResourceLocation TIME_COMMANDS = new ResourceLocation("jedt:time_commands");

    /**
     * The identifier of the 'Game Mode' menu in the 'Quick Commands' menu.
     */
    ResourceLocation GAMEMODE_COMMANDS = new ResourceLocation("jedt:gamemode_commands");

    /**
     * The identifier of the 'Weather' menu in the 'Quick Commands' menu.
     */
    ResourceLocation WEATHER_COMMANDS = new ResourceLocation("jedt:weather_commands");

    /**
     * The identifier of the 'Difficulty' menu in the 'Quick Commands' menu.
     */
    ResourceLocation DIFFICULTY_COMMANDS = new ResourceLocation("jedt:difficulty_commands");

    /**
     * The identifier of the 'Random Ticks' menu in the 'Quick Commands' menu.
     */
    ResourceLocation TICK_SPEED_COMMANDS = new ResourceLocation("jedt:tick_speed_commands");

    /**
     * The identifier of the 'Misc' menu in the 'Quick Commands' menu.
     */
    ResourceLocation MISC_COMMANDS = new ResourceLocation("jedt:misc_commands");

    /**
     * The identifier of the 'Actions' menu.
     */
    ResourceLocation ACTIONS = new ResourceLocation("jedt:actions");

    /**
     * The identifier of the 'Copy' menu.
     */
    ResourceLocation COPY = new ResourceLocation("jedt:copy");

    /**
     * The identifier of the 'Display' menu.
     */
    ResourceLocation DISPLAY = new ResourceLocation("jedt:display");

    /**
     * The identifier of the 'GameTest' menu.
     */
    ResourceLocation GAMETEST = new ResourceLocation("jedt:gametest");

    /**
     * Returns a {@link Component} to display in the header of this menu. By default, this is a
     * {@linkplain Component#translatable(String) translatable component} with the translation key
     * {@code debug.menu.[namespace].[menu name]}, with the namespace and menu name that were given in
     * {@link MenuManager#getMenu}.
     *
     * @since 0.1
     */
    Component getHeader();

    /**
     * Returns a stream of all the {@linkplain Item options} in this menu. Options can be added via {@link #addOption}.
     *
     * @see #addOption(Item)
     * @since 0.1
     */
    Stream<Item> options();

    /**
     * Adds a {@link Item} to this menu.
     *
     * @param option The option to add to this menu. Must not be null.
     * @see #options()
     * @since 0.1
     */
    void addOption(Item option);
}
