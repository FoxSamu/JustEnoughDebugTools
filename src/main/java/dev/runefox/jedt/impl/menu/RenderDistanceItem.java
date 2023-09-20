package dev.runefox.jedt.impl.menu;

import dev.runefox.jedt.api.menu.NumberItem;
import dev.runefox.jedt.api.menu.OptionSelectContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class RenderDistanceItem extends NumberItem {
    public RenderDistanceItem(Component name) {
        super(name);
    }

    @Override
    protected int get() {
        return Minecraft.getInstance().options.renderDistance().get();
    }

    @Override
    protected void mutate(int delta, OptionSelectContext context) {
        Minecraft client = context.minecraft();
        OptionInstance<Integer> opt = Minecraft.getInstance().options.renderDistance();
        OptionInstance.IntRange range = (OptionInstance.IntRange) opt.values();
        Minecraft.getInstance().options.renderDistance().set(
            Mth.clamp(
                client.options.renderDistance().get() + delta,
                range.minInclusive(),
                range.maxInclusive()
            )
        );
    }
}