package net.shadew.debug.api.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

public class BooleanGameruleOption extends BooleanOption {
    private final String command;
    private final Text response;
    private final GameRules.Key<GameRules.BooleanRule> key;

    public BooleanGameruleOption(Text name, GameRules.Key<GameRules.BooleanRule> key, Text response) {
        super(name);
        this.command = "gamerule " + key + " ";
        this.response = response;
        this.key = key;
    }

    public BooleanGameruleOption(Text name, GameRules.Key<GameRules.BooleanRule> key) {
        this(name, key, null);
    }

    @Override
    protected void toggle(OptionSelectContext context) {
        if (!context.hasPermissionLevel(2)) {
            context.spawnResponse(
                new TranslatableText("debug.options.debug.commands.no_permission")
                    .formatted(Formatting.RED)
            );
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        assert client.world != null;

        boolean newVal = !get();
        context.sendCommand(command + newVal);

        // Temporarily set the gamerule on the client - the server is gonna send an update but we want smooth toggling
        // so we don't wait for the server (if we do wait the button might be pressed twice without toggling)
        client.world.getGameRules().get(key).set(newVal, null);

        if (response != null) {
            context.spawnResponse(response);
        }
    }

    @Override
    protected boolean get() {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.world != null;
        return client.world.getGameRules().getBoolean(key);
    }
}
