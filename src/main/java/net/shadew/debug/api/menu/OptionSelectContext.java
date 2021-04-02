package net.shadew.debug.api.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public interface OptionSelectContext {
    void spawnResponse(Component response);
    void openMenu(DebugMenu menu);

    void copyToClipboard(String text);
    String getClipboard();

    void closeScreen();
    void closeMenu();

    default void spawnResponse(String key, Object... values) {
        spawnResponse(new TranslatableComponent(key, values));
    }

    default boolean reducedDebug() {
        return minecraft().showOnlyReducedInfo();
    }

    default boolean hasPermissionLevel(int level) {
        Minecraft client = minecraft();
        assert client.player != null;
        return client.player.hasPermissions(level);
    }

    default void sendCommand(String command) {
        if (!command.startsWith("/")) {
            command = "/" + command;
        }
        Minecraft client = minecraft();
        assert client.player != null;
        client.player.chat(command);
    }

    // Number option context
    int delta();

    Minecraft minecraft();

    boolean screenPauses();
    void setScreenPauses(boolean pause);
}
