package net.shadew.debug.api.menu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class CommandOption extends ActionOption {
    private final String command;
    private final Component response;

    public CommandOption(Component name, String command, Component response) {
        super(name);
        this.command = command;
        this.response = response;
    }

    public CommandOption(Component name, String command) {
        this(name, command, null);
    }

    @Override
    public void onClick(OptionSelectContext context) {
        if (!context.hasPermissionLevel(2)) {
            context.spawnResponse(
                new TranslatableComponent("debug.options.debug.commands.no_permission")
                    .withStyle(ChatFormatting.RED)
            );
            return;
        }
        context.sendCommand(command);
        if (response != null) {
            context.spawnResponse(response);
        }
    }
}
