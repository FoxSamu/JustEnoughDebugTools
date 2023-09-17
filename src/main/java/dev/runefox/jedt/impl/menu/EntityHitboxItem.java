package dev.runefox.jedt.impl.menu;

import dev.runefox.jedt.api.menu.BooleanItem;
import dev.runefox.jedt.api.menu.OptionSelectContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

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
