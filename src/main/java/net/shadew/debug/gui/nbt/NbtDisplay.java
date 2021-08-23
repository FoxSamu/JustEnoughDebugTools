package net.shadew.debug.gui.nbt;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.nbt.Tag;

public class NbtDisplay<T extends Tag> extends GuiComponent implements Widget, GuiEventListener {
    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float dt) {

    }
}
