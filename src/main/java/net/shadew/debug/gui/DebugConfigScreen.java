package net.shadew.debug.gui;

import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL32;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

import net.shadew.debug.DebugClient;
import net.shadew.debug.api.menu.*;
import net.shadew.debug.mixin.ScreenAccessor;

public class DebugConfigScreen extends Screen {
    public static final DebugConfigScreen INSTANCE = new DebugConfigScreen();

    private final List<ConfigMenu> menus = new ArrayList<>();
    private final List<HoverText> hoverTexts = new ArrayList<>();
    private final ClipboardManager clipboard = new ClipboardManager();
    private final DescriptionBox descriptionBox = new DescriptionBox();

    private boolean pauses;

    public DebugConfigScreen() {
        super(new TranslatableComponent("jedt.options"));
    }

    @Override
    protected void init() {
        super.init();
        assert minecraft != null;

        if (!hasOpenMenus()) {
            openMenu(DebugClient.ROOT_MENU, 0);

            children().clear();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return pauses;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.width = width;
        this.height = height;
        this.minecraft = minecraft;
        descriptionBox.resizeScreen(width, height);
    }

    private void openMenu(DebugMenu debugMenu, int index) {
        ConfigMenu configMenu = new ConfigMenu(debugMenu.getHeader());
        debugMenu.options()
                 .filter(DebugOption::isVisible)
                 .map(opt -> entryFromOption(opt, index))
                 .forEach(configMenu::addEntry);
        openMenu(configMenu, index);
    }

    private ConfigMenu.Entry entryFromOption(DebugOption option, int index) {
        OptionType type = option.getType();
        IntConsumer handler = incr -> {
            SelectionContext context = new SelectionContext(incr, index + 1);
            option.onClick(context);
        };

        return switch (type) {
            case ACTION -> new ConfigMenu.Entry(option, option.getName(), () -> handler.accept(0), option::getDisplayValue);
            case MENU -> new ConfigMenu.MenuEntry(option, option.getName(), () -> handler.accept(0), option::getDisplayValue);
            case BOOLEAN -> new ConfigMenu.CheckableEntry(option, option.getName(), () -> handler.accept(0), option::hasCheck, option::getDisplayValue);
            case NUMBER -> new ConfigMenu.SpinnerEntry(option, option.getName(), () -> handler.accept(Screen.hasShiftDown() ? -1 : 1), () -> handler.accept(1), () -> handler.accept(-1), option::getDisplayValue);
            case EXTERNAL -> new ConfigMenu.Entry(option, option.getName(), () -> handler.accept(0), option::getDisplayValue, 4);
        };
    }

    private void spawnHoverText(Component text) {
        assert minecraft != null;
        int mouseX = (int) (minecraft.mouseHandler.xpos() * (double) minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getWidth());
        int mouseY = (int) (minecraft.mouseHandler.ypos() * (double) minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getHeight());

        int rx = (int) (Math.random() * 30 - 15);
        int ry = (int) (Math.random() * 30 - 15);

        HoverText txt = new HoverText(text, mouseX + rx, mouseY + ry);
        hoverTexts.add(txt);
    }

    private void closeMenusFrom(int index) {
        for (int i = index, s = menus.size(); i < s; i++) {
            menus.get(i).closeQuietly();
        }
    }

    private void openMenu(ConfigMenu menu, int index) {
        menu.open();
        if (index < menus.size()) {
            menu.forceVisible(false);
            ConfigMenu old = menus.set(index, menu);
            old.swapWith(menu);
        } else {
            menus.add(menu);
        }

        menu.setCloseHandler(() -> {
            if (index == 0) {
                onClose();
            }
            closeMenusFrom(index);
        });

        closeMenusFrom(index + 1);
    }

    private boolean isInvalidOverlayScreen(Screen screen) {
        if (screen == this) {
            return false;
        }

        if (screen instanceof DebugMenuOverlayScreen) {
            return ((DebugMenuOverlayScreen) screen).getParentScreen() != this;
        }

        return screen == null;
    }

    public void receiveTick() {
        // Close our menus if we're not literally overlayed
        if (minecraft == null || isInvalidOverlayScreen(minecraft.screen)) {
            for (ConfigMenu menu : menus) {
                menu.closeQuietly();
            }
        }
        menus.forEach(ConfigMenu::tick);
        menus.removeIf(ConfigMenu::isFullyClosed);

        children().clear();
        ((ScreenAccessor) this).getChildren().addAll(menus);

        hoverTexts.removeIf(txt -> txt.existTime-- < 0);
    }

    private int getTotalWidth(float partialTicks) {
        return menus.stream().mapToInt(menu -> menu.getDisplayableWidth(partialTicks)).sum();
    }

    public void receiveRender(PoseStack pose, int mouseX, int mouseY, float tickProgress) {
        assert minecraft != null;

        RenderSystem.clear(GL32.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);

        pose.pushPose();
        pose.translate(0, 0, 110);
        RenderSystem.disableDepthTest();

        int totalWidth = getTotalWidth(tickProgress);

        int oversize = Math.max(0, totalWidth - width);
        int leftOffset = -oversize;

        boolean clearDescriptionBox = true;
        for (ConfigMenu menu : menus) {
            if (menu.canRender()) {
                int w = menu.getDisplayableWidth(tickProgress);
                menu.setHeight(height);
                menu.setLeftOffset(leftOffset);
                if (menu.render(pose, mouseX, mouseY, tickProgress, descriptionBox)) {
                    clearDescriptionBox = false;
                }
                leftOffset += w;
            }
        }

        pose.translate(0, 0, 5);

        for (HoverText txt : hoverTexts) {
            float alpha = txt.existTime / 30f;
            int color = (int) (Mth.clamp(alpha, 0, 1) * 255) << 24;

            if ((color & 0xFC000000) != 0) {
                int width = minecraft.font.width(txt.text);
                drawHoverTextBackground(pose, txt.x - width / 2 - 5, txt.y - 9, txt.x + width / 2 + 5, txt.y + 9, alpha);
                drawString(pose, minecraft.font, txt.text, txt.x - width / 2, txt.y - 4, color | 0xFFFFFF);
            }
        }

        pose.translate(0, 0, 5);
        if (clearDescriptionBox) {
            descriptionBox.updateHovered(null, 0, 0, 0, 0, width, height);
        }
        descriptionBox.render(pose, mouseX, mouseY, tickProgress);

        RenderSystem.enableDepthTest();
        pose.popPose();

    }

    private void drawHoverTextBackground(PoseStack matrices, int x1, int y1, int x2, int y2, float alpha) {
        float bgAlpha = alpha / 2;
        int color = (int) (Mth.clamp(alpha, 0, 1) * 255) << 24;
        int lighterColor = color | 0xAAAAAA;
        int darkerColor = color | 0x777777;
        int bgColor = (int) (Mth.clamp(bgAlpha, 0, 1) * 255) << 24;

        RenderSystem.enableBlend();
        // Inner fill (transparent black)
        fill(matrices, x1 + 2, y1 + 2, x2 - 2, y2 - 2, bgColor);

        // Inner border (grey)
        fill(matrices, x1 + 1, y1 + 1, x2 - 1, y1 + 2, lighterColor);
        fill(matrices, x1 + 1, y2 - 2, x2 - 1, y2 - 1, darkerColor);
        fillGradient(matrices, x1 + 1, y1 + 2, x1 + 2, y2 - 2, lighterColor, darkerColor);
        fillGradient(matrices, x2 - 2, y1 + 2, x2 - 1, y2 - 2, lighterColor, darkerColor);

        // Outer border (black)
        fill(matrices, x1 + 1, y1, x2 - 1, y1 + 1, color);
        fill(matrices, x1 + 1, y2 - 1, x2 - 1, y2, color);
        fill(matrices, x1, y1 + 1, x1 + 1, y2 - 1, color);
        fill(matrices, x2 - 1, y1 + 1, x2, y2 - 1, color);
    }

    private void closeLastInteractiveMenu() {
        for (int i = menus.size() - 1; i >= 0; i--) {
            ConfigMenu menu = menus.get(i);
            if (menu.canInteract()) {
                menu.close();
                return;
            }
        }
    }

    private boolean hasOpenMenus() {
        for (ConfigMenu menu : menus) {
            if (!menu.isClosed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            closeLastInteractiveMenu();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_R && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            for (ConfigMenu menu : menus) {
                menu.closeQuietly();
            }
            DebugClient.reloadMenus();
            openMenu(DebugClient.ROOT_MENU, 0);
            children().clear();
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (DebugClient.debugOptionsKey.matches(keyCode, scanCode)) {
            DebugClient.f6KeyDown = true;
            onClose();
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        for (ConfigMenu menu : menus) {
            menu.closeQuietly();
        }
    }

    public static void show() {
        Minecraft.getInstance().setScreen(INSTANCE);
    }

    private static class HoverText {
        private final Component text;
        private final int x, y;
        private int existTime = 30;

        private HoverText(Component text, int x, int y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }
    }

    private class SelectionContext implements OptionSelectContext {
        private final int increment;
        private final int index;

        private SelectionContext(int increment, int index) {
            this.increment = increment;
            this.index = index;
        }

        @Override
        public void spawnResponse(Component response) {
            spawnHoverText(response);
        }

        @Override
        public void openMenu(DebugMenu menu) {
            DebugConfigScreen.this.openMenu(menu, index);
        }

        @Override
        public void copyToClipboard(String text) {
            assert minecraft != null;
            clipboard.setClipboard(minecraft.getWindow().getWindow(), text);
        }

        @Override
        public String getClipboard() {
            assert minecraft != null;
            return clipboard.getClipboard(minecraft.getWindow().getWindow(), null);
        }

        @Override
        public void closeScreen() {
            onClose();
        }

        @Override
        public void closeMenu() {
            closeMenusFrom(index);
        }

        @Override
        public int delta() {
            return increment;
        }

        @Override
        public Minecraft minecraft() {
            assert minecraft != null;
            return minecraft;
        }

        @Override
        public boolean screenPauses() {
            return pauses;
        }

        @Override
        public void setScreenPauses(boolean pause) {
            pauses = pause;
        }

        @Override
        public void openScreen(Screen screen) {
            minecraft().setScreen(screen);
        }

        @Override
        public Screen debugMenuScreen() {
            return DebugConfigScreen.this;
        }
    }
}
