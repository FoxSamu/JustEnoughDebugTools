package net.shadew.debug.api.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public interface OptionSelectContext {
    void spawnResponse(Text response);
    void openMenu(DebugMenu menu);

    void copyToClipboard(String text);
    String getClipboard();

    void closeScreen();
    void closeMenu();

    default void spawnResponse(String key, Object... values) {
        spawnResponse(new TranslatableText(key, values));
    }

    default boolean reducedDebug() {
        return client().hasReducedDebugInfo();
    }

    default boolean hasPermissionLevel(int level) {
        MinecraftClient client = client();
        assert client.player != null;
        return client.player.hasPermissionLevel(level);
    }

    default void sendCommand(String command) {
        if (!command.startsWith("/")) {
            command = "/" + command;
        }
        MinecraftClient client = client();
        assert client.player != null;
        client.player.sendChatMessage(command);
    }

    // Number option context
    int delta();

    MinecraftClient client();

    boolean screenPauses();
    void setScreenPauses(boolean pause);
}
