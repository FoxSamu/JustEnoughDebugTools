package net.shadew.debug.api.menu;

import net.minecraft.resources.ResourceLocation;

import net.shadew.debug.api.DebugMenuInitializer;

/**
 * A manager for {@link DebugMenu} instances. {@link DebugMenu} instances can only be obtained via an instance of this
 * interface. A {@link DebugMenuManager} instance is passed into each {@link DebugMenuInitializer}. For compatibility
 * with other mods using this mod and interaction with the builtin menus, menus are all identified by a {@link
 * ResourceLocation}. The identifiers of the default menus are specified as constants in the {@link DebugMenu}
 * interface.
 *
 * @author Shadew
 * @see DebugMenu
 * @see DebugMenuInitializer
 * @since 0.1
 */
public interface DebugMenuManager {
    /**
     * Gets or creates a {@link DebugMenu} with the given identifier. The identifier is parsed as a {@link
     * ResourceLocation} and immediately redirected to {@link #getMenu(ResourceLocation)}.
     *
     * @param name The menu identifier, or null to get the root ({@code debug:root}) menu
     * @return The obtained {@link DebugMenu} instance
     *
     * @since 0.1
     */
    default DebugMenu getMenu(String name) {
        return getMenu(name == null ? null : new ResourceLocation(name));
    }

    /**
     * Gets or creates a {@link DebugMenu} with the given identifier.
     *
     * @param name The menu identifier, or null to get the root ({@code debug:root}) menu
     * @return The obtained {@link DebugMenu} instance
     *
     * @since 0.1
     */
    DebugMenu getMenu(ResourceLocation name);
}
