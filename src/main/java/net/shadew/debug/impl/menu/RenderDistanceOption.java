package net.shadew.debug.impl.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.Option;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import net.shadew.debug.api.menu.NumberOption;
import net.shadew.debug.api.menu.OptionSelectContext;

public class RenderDistanceOption extends NumberOption {
    public RenderDistanceOption(Text name) {
        super(name);
    }

    @Override
    protected int getValue() {
        return (int) Option.RENDER_DISTANCE.get(MinecraftClient.getInstance().options);
    }

    @Override
    protected void mutateValue(int delta, OptionSelectContext context) {
        MinecraftClient client = context.client();
        Option.RENDER_DISTANCE.set(
            client.options, MathHelper.clamp(
                client.options.viewDistance + delta,
                Option.RENDER_DISTANCE.getMin(),
                Option.RENDER_DISTANCE.getMax()
            )
        );
    }
}
