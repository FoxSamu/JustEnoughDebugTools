package dev.runefox.jedt.impl.menu;

import dev.runefox.jedt.api.menu.BooleanItem;
import dev.runefox.jedt.api.menu.OptionSelectContext;
import dev.runefox.jedt.mixin.DebugRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ChunkBordersItem extends BooleanItem {
    public ChunkBordersItem(Component name) {
        super(name);
    }

    @Override
    protected void toggle(OptionSelectContext context) {
        context.minecraft().debugRenderer.switchRenderChunkborder();
    }

    @Override
    protected boolean get() {
        return ((DebugRendererAccessor) Minecraft.getInstance().debugRenderer).debug_getRenderChunkBorder();
    }
}
