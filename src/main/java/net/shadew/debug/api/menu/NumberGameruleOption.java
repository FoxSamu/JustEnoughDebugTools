package net.shadew.debug.api.menu;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameRules;

public class NumberGameruleOption extends NumberOption {
    private final String command;
    private final Component response;
    private final GameRules.Key<GameRules.IntegerValue> key;

    public NumberGameruleOption(Component name, GameRules.Key<GameRules.IntegerValue> key, Component response) {
        super(name);
        this.command = "gamerule " + key + " ";
        this.response = response;
        this.key = key;
    }

    public NumberGameruleOption(Component name, GameRules.Key<GameRules.IntegerValue> key) {
        this(name, key, null);
    }

    @Override
    protected int getValue() {
        Minecraft client = Minecraft.getInstance();
        assert client.level != null;
        return client.level.getGameRules().getInt(key);
    }

    @Override
    protected void mutateValue(int delta, OptionSelectContext context) {
        if (!context.hasPermissionLevel(2)) {
            context.spawnResponse(
                new TranslatableComponent("debug.options.debug.commands.no_permission")
                    .withStyle(ChatFormatting.RED)
            );
            return;
        }

        Minecraft client = Minecraft.getInstance();
        assert client.level != null;

        int newVal = getValue() + delta;
        context.sendCommand(command + newVal);

        // Temporarily set the gamerule on the client - the server is gonna send an update but we want smooth switching
        // so we don't wait for the server (if we do wait the button might be changed twice without updating)
        client.level.getGameRules().getRule(key).set(newVal, null);

        if (response != null) {
            context.spawnResponse(response);
        }
    }
}
