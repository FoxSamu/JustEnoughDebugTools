package net.shadew.debug.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class Label extends AbstractButton {
    public Label(int x, int y, int width, int height, Component text) {
        super(x, y, width, height, text);
    }

    @Override
    public void onPress() {
    }


    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        graphics.setColor(1, 1, 1, 1);
        int colour = 0xFFFFFF;
        renderString(graphics, minecraft.font, colour | Mth.ceil(alpha * 255.0F) << 24);
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
        return null;
    }

    @Override
    public void playDownSound(SoundManager sndMgr) {
    }


    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, getMessage());
    }
}
