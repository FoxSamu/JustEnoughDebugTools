package dev.runefox.jedt.api.menu;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class CommandItem extends ActionItem {
    private final String command;
    private final Component response;
    private final Component commandDesc;
    private int closeOnClick;

    public CommandItem(Component name, String command, Component response) {
        super(name);
        this.command = command;
        this.response = response;
        this.commandDesc = Component.literal((command.startsWith("/") ? "" : "/") + command).withStyle(ChatFormatting.AQUA);
    }

    public CommandItem(Component name, String command) {
        this(name, command, null);
    }

    @Override
    public Component getDescription() {
        MutableComponent desc = Component.empty().append(commandDesc);
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
                Component.translatable("debug.options.jedt.commands.no_permission")
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

    public CommandItem closeScreenOnClick() {
        closeOnClick = 2;
        return this;
    }

    public CommandItem closeMenuOnClick() {
        closeOnClick = 1;
        return this;
    }

    public CommandItem dontCloseOnClick() {
        closeOnClick = 0;
        return this;
    }
}
