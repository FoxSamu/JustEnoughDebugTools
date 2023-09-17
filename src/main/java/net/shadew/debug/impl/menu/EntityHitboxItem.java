package net.shadew.debug.impl.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import net.shadew.debug.api.menu.BooleanItem;
import net.shadew.debug.api.menu.OptionSelectContext;

public class EntityHitboxItem extends BooleanItem {
    public EntityHitboxItem(Component name) {
        super(name);
    }

    @Override
    protected void toggle(OptionSelectContext context) {
        boolean bl = !context.minecraft().getEntityRenderDispatcher().shouldRenderHitBoxes();
        context.minecraft().getEntityRenderDispatcher().setRenderHitBoxes(bl);
    }

    @Override
    protected boolean get() {
        return Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes();
    }
}
