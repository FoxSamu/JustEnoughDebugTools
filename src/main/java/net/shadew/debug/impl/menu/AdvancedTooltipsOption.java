package net.shadew.debug.impl.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import net.shadew.debug.api.menu.BooleanOption;
import net.shadew.debug.api.menu.OptionSelectContext;
import net.shadew.debug.render.DebugRenderers;

public class AdvancedTooltipsOption extends BooleanOption {
    public AdvancedTooltipsOption(Text name) {
        super(name);
    }

    @Override
    protected void toggle(OptionSelectContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.options.advancedItemTooltips = !client.options.advancedItemTooltips;
        client.options.write();
    }

    @Override
    protected boolean get() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.options.advancedItemTooltips;
    }
}
