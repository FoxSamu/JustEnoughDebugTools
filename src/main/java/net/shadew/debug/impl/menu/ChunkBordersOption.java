package net.shadew.debug.impl.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import net.shadew.debug.api.menu.BooleanOption;
import net.shadew.debug.api.menu.OptionSelectContext;
import net.shadew.debug.mixin.DebugRendererAccessor;

public class ChunkBordersOption extends BooleanOption {
    public ChunkBordersOption(Text name) {
        super(name);
    }

    @Override
    protected void toggle(OptionSelectContext context) {
        context.client().debugRenderer.toggleShowChunkBorder();
    }

    @Override
    protected boolean get() {
        return ((DebugRendererAccessor) MinecraftClient.getInstance().debugRenderer).getShowChunkBorder();
    }
}
