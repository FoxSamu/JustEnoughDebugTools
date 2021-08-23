package net.shadew.debug.api.menu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class CommandOption extends ActionOption {
    private final String command;
    private final Component response;
    private final Component commandDesc;
    private int closeOnClick;

    public CommandOption(Component name, String command, Component response) {
        super(name);
        this.command = command;
        this.response = response;
        this.commandDesc = new TextComponent((command.startsWith("/") ? "" : "/") + command).withStyle(ChatFormatting.AQUA);
    }

    public CommandOption(Component name, String command) {
        this(name, command, null);
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
    public void onClick(OptionSelectContext context) {
        if (!context.hasPermissionLevel(2)) {
            context.spawnResponse(
                new TranslatableComponent("debug.options.jedt.commands.no_permission")
                    .withStyle(ChatFormatting.RED)
            );
            return;
        }
        context.sendCommand(command);
        if (response != null) {
            context.spawnResponse(response);
        }
        if (closeOnClick == 2) {
            context.closeScreen();
        }
        if (closeOnClick == 1) {
            context.closeMenu();
        }
    }

    public CommandOption closeScreenOnClick() {
        closeOnClick = 2;
        return this;
    }

    public CommandOption closeMenuOnClick() {
        closeOnClick = 1;
        return this;
    }

    public CommandOption dontCloseOnClick() {
        closeOnClick = 0;
        return this;
    }
}
