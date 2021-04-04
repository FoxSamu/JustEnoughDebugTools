package net.shadew.debug.api.menu;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameRules;

public class BooleanGameruleOption extends BooleanOption {
    private final String command;
    private final Component response;
    private final GameRules.Key<GameRules.BooleanValue> key;
    private final Component commandDesc;

    public BooleanGameruleOption(Component name, GameRules.Key<GameRules.BooleanValue> key, Component response) {
        super(name);
        this.command = "gamerule " + key + " ";
        this.response = response;
        this.key = key;
        this.commandDesc = new TextComponent("/" + command + "<x>").withStyle(ChatFormatting.AQUA);
    }

    public BooleanGameruleOption(Component name, GameRules.Key<GameRules.BooleanValue> key) {
        this(name, key, null);
    }

    @Override
    public Component getDescription() {
        MutableComponent desc = new TextComponent("").append(commandDesc);
        Component superDesc = super.getDescription();
        if (superDesc != null) {
            desc.append("\n");
            desc.append(superDesc);
        }
        return desc;
    }

    @Override
    protected void toggle(OptionSelectContext context) {
        if (!context.hasPermissionLevel(2)) {
            context.spawnResponse(
                new TranslatableComponent("debug.options.debug.commands.no_permission")
                    .withStyle(ChatFormatting.RED)
            );
            return;
        }

        Minecraft client = Minecraft.getInstance();
        assert client.level != null;

        boolean newVal = !get();
        context.sendCommand(command + newVal);

        // Temporarily set the gamerule on the client - the server is gonna send an update but we want smooth toggling
        // so we don't wait for the server (if we do wait the button might be pressed twice without toggling)
        client.level.getGameRules().getRule(key).set(newVal, null);

        if (response != null) {
            context.spawnResponse(response);
        }
    }

    @Override
    protected boolean get() {
        Minecraft client = Minecraft.getInstance();
        assert client.level != null;
        return client.level.getGameRules().getBoolean(key);
    }
}
