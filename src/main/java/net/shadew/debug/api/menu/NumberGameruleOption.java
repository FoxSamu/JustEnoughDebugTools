package net.shadew.debug.api.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

import net.shadew.debug.mixin.GameRulesIntRuleAccessor;

public class NumberGameruleOption extends NumberOption {
    private final String command;
    private final Text response;
    private final GameRules.Key<GameRules.IntRule> key;

    public NumberGameruleOption(Text name, GameRules.Key<GameRules.IntRule> key, Text response) {
        super(name);
        this.command = "gamerule " + key + " ";
        this.response = response;
        this.key = key;
    }

    public NumberGameruleOption(Text name, GameRules.Key<GameRules.IntRule> key) {
        this(name, key, null);
    }

    @Override
    protected int getValue() {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.world != null;
        return client.world.getGameRules().getInt(key);
    }

    @Override
    protected void mutateValue(int delta, OptionSelectContext context) {
        if (!context.hasPermissionLevel(2)) {
            context.spawnResponse(
                new TranslatableText("debug.options.debug.commands.no_permission")
                    .formatted(Formatting.RED)
            );
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        assert client.world != null;

        int newVal = getValue() + delta;
        context.sendCommand(command + newVal);

        // Temporarily set the gamerule on the client - the server is gonna send an update but we want smooth toggling
        // so we don't wait for the server (if we do wait the button might be pressed twice without toggling)
        ((GameRulesIntRuleAccessor) client.world.getGameRules().get(key)).setRuleValue(newVal);

        if (response != null) {
            context.spawnResponse(response);
        }
    }
}
