package net.shadew.debug.impl.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import net.shadew.debug.api.menu.BooleanOption;
import net.shadew.debug.api.menu.OptionSelectContext;
import net.shadew.debug.mixin.DebugRendererAccessor;

public class ChunkBordersOption extends BooleanOption {
    public ChunkBordersOption(Component name) {
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
