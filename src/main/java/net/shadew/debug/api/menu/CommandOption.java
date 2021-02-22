package net.shadew.debug.api.menu;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class CommandOption extends ActionOption {
    private final String command;
    private final Text response;

    public CommandOption(Text name, String command, Text response) {
        super(name);
        this.command = command;
        this.response = response;
    }

    public CommandOption(Text name, String command) {
        this(name, command, null);
    }

    @Override
    public void onClick(OptionSelectContext context) {
        if (!context.hasPermissionLevel(2)) {
            context.spawnResponse(
                new TranslatableText("debug.options.debug.commands.no_permission")
                    .formatted(Formatting.RED)
            );
            return;
        }
        context.sendCommand(command);
        if (response != null) {
            context.spawnResponse(response);
        }
    }
}
