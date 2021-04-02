package net.shadew.debug.impl.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import net.shadew.debug.api.menu.NumberOption;
import net.shadew.debug.api.menu.OptionSelectContext;

public class RenderDistanceOption extends NumberOption {
    public RenderDistanceOption(Component name) {
        super(name);
    }

    @Override
    protected int getValue() {
        return (int) Option.RENDER_DISTANCE.get(Minecraft.getInstance().options);
    }

    @Override
    protected void mutateValue(int delta, OptionSelectContext context) {
        Minecraft client = context.minecraft();
        Option.RENDER_DISTANCE.set(
            client.options, Mth.clamp(
                client.options.renderDistance + delta,
                Option.RENDER_DISTANCE.getMinValue(),
                Option.RENDER_DISTANCE.getMaxValue()
            )
        );
    }
}
