package net.shadew.debug.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class Label extends AbstractButton {
    public Label(int x, int y, int width, int height, Component text) {
        super(x, y, width, height, text);
    }

    @Override
    public void onPress() {
    }

    @Override
    public void renderButton(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        drawString(
            matrix, font, getMessage(),
            x,
            y + (height - 8) / 2,
            0xFFFFFF | Mth.ceil(alpha * 255) << 24
        );
    }

    @Override
    public boolean changeFocus(boolean forward) {
        return false;
    }

    @Override
    public void playDownSound(SoundManager sndMgr) {
    }
}
