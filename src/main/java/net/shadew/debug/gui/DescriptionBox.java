package net.shadew.debug.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.List;

import net.shadew.debug.api.menu.DebugOption;

public class DescriptionBox extends GuiComponent implements Widget {
    private static final int WIDTH = 200;
    private static final int MARGIN = 5;
    private static final int LINE_HEIGHT = 10;

    private DebugOption hovered;
    private long hoverTimeStart;
    private boolean visible = false;
    private int x;
    private int y;
    private int width;
    private int height;
    private int hoverX;
    private int hoverY;
    private int hoverW;
    private int hoverH;
    private Component header;
    private Component desc;
    private List<FormattedCharSequence> headerLines;
    private List<FormattedCharSequence> lines;

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float tickProgress) {
        if (visible) {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;

            int x1 = x;
            int y1 = y;
            int x2 = x1 + width;
            int y2 = y1 + height;

            drawDescriptionBackground(pose, x1, y1, x2, y2, 1);

            int px = x1 + MARGIN;
            int py = y1 + MARGIN;

            if (headerLines != null) {
                for (FormattedCharSequence seq : headerLines) {
                    font.drawShadow(pose, seq, px, py, 0xFFFF88);
                    py += LINE_HEIGHT;
                }

                if (lines != null && headerLines != null && !headerLines.isEmpty() && !lines.isEmpty()) {
                    py += MARGIN;
                }
            }

            if (lines != null) {
                for (FormattedCharSequence seq : lines) {
                    font.drawShadow(pose, seq, px, py, 0xFFFFFF);
                    py += LINE_HEIGHT;
                }
            }
        }
    }

    public void resizeScreen(int w, int h) {
        if (visible)
            update(hoverX, hoverY, hoverW, hoverH, w, h, header, desc);
    }

    public void updateHovered(DebugOption hovered, int hoverX, int hoverY, int hoverW, int hoverH, int scrW, int scrH) {
        if (hovered != this.hovered) {
            this.visible = false;
            this.hoverTimeStart = System.currentTimeMillis();
            this.hovered = hovered;
        } else if (this.hovered != null) {
            if (System.currentTimeMillis() - hoverTimeStart >= 1000) {
                if (!visible || hoverX != this.hoverX || hoverY != this.hoverY || hoverW != this.hoverW || hoverH != this.hoverH) {
                    visible = true;
                    Component header = hovered.getLongName();
                    Component desc = hovered.getDescription();
                    if (header == null)
                        header = hovered.getName();
                    update(hoverX, hoverY, hoverW, hoverH, scrW, scrH, header, desc);
                }
            }
        }
    }

    protected void update(int hoverX, int hoverY, int hoverW, int hoverH, int scrW, int scrH, Component header, Component desc) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        this.header = header;
        this.desc = desc;
        this.width = Math.min(scrW, WIDTH);
        this.height = 2 * MARGIN;
        this.headerLines = null;
        this.lines = null;
        this.hoverX = hoverX;
        this.hoverY = hoverY;
        this.hoverW = hoverW;
        this.hoverH = hoverH;

        int wrapSize = width - 2 * MARGIN;
        if (header != null) {
            headerLines = font.split(header, wrapSize);
            height += headerLines.size() * LINE_HEIGHT;
        }

        if (desc != null) {
            lines = font.split(desc, wrapSize);

            if (header != null && !headerLines.isEmpty() && !lines.isEmpty()) {
                height += MARGIN;
            }

            height += lines.size() * LINE_HEIGHT;
        }

        this.x = hoverX;
        this.y = hoverY + hoverH;

        if (x + width > scrW) {
            x = Math.max(0, hoverX + hoverW - width);
        }
        if (y + height > scrH) {
            y = hoverY - height;
        }
    }

    private void drawDescriptionBackground(PoseStack pose, int x1, int y1, int x2, int y2, float alpha) {
        float bgAlpha = alpha * 0.75f;
        int color = (int) (Mth.clamp(alpha, 0, 1) * 255) << 24;
        int lighterColor = color | 0xAAAAAA;
        int darkerColor = color | 0x777777;
        int bgColor = (int) (Mth.clamp(bgAlpha, 0, 1) * 255) << 24;

        RenderSystem.enableBlend();
        // Inner fill (transparent black)
        fill(pose, x1 + 2, y1 + 2, x2 - 2, y2 - 2, bgColor);

        // Inner border (grey)
        fill(pose, x1 + 1, y1 + 1, x2 - 1, y1 + 2, lighterColor);
        fill(pose, x1 + 1, y2 - 2, x2 - 1, y2 - 1, darkerColor);
        fillGradient(pose, x1 + 1, y1 + 2, x1 + 2, y2 - 2, lighterColor, darkerColor);
        fillGradient(pose, x2 - 2, y1 + 2, x2 - 1, y2 - 2, lighterColor, darkerColor);

        // Outer border (black)
        fill(pose, x1 + 1, y1, x2 - 1, y1 + 1, color);
        fill(pose, x1 + 1, y2 - 1, x2 - 1, y2, color);
        fill(pose, x1, y1 + 1, x1 + 1, y2 - 1, color);
        fill(pose, x2 - 1, y1 + 1, x2, y2 - 1, color);
    }
}
