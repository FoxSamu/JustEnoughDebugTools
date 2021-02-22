package net.shadew.debug.impl.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import net.shadew.debug.api.menu.BooleanOption;
import net.shadew.debug.api.menu.OptionSelectContext;

public class EntityHitboxOption extends BooleanOption {
    public EntityHitboxOption(Text name) {
        super(name);
    }

    @Override
    protected void toggle(OptionSelectContext context) {
        boolean bl = !context.client().getEntityRenderDispatcher().shouldRenderHitboxes();
        context.client().getEntityRenderDispatcher().setRenderHitboxes(bl);
    }

    @Override
    protected boolean get() {
        return MinecraftClient.getInstance().getEntityRenderDispatcher().shouldRenderHitboxes();
    }
}
