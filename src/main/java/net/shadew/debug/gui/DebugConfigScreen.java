package net.shadew.debug.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Clipboard;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

import net.shadew.debug.DebugClient;
import net.shadew.debug.api.menu.DebugMenu;
import net.shadew.debug.api.menu.DebugOption;
import net.shadew.debug.api.menu.OptionSelectContext;
import net.shadew.debug.api.menu.OptionType;

public class DebugConfigScreen extends Screen {
    public static final DebugConfigScreen INSTANCE = new DebugConfigScreen();

    private final List<ConfigMenu> menus = new ArrayList<>();
    private final List<HoverText> hoverTexts = new ArrayList<>();
    private final Clipboard clipboard = new Clipboard();

    private boolean pauses;

    public DebugConfigScreen() {
        super(new TranslatableText("debug.options"));
    }

    @Override
    protected void init() {
        super.init();
        assert client != null;

        openMenu(DebugClient.ROOT_MENU, 0);

        children.clear();
    }

    @Override
    public boolean isPauseScreen() {
        return pauses;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.width = width;
        this.height = height;
        this.client = client;
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

        switch (type) {
            case ACTION:
                return new ConfigMenu.Entry(option.getName(), () -> handler.accept(0), option::getDisplayValue);
            case MENU:
                return new ConfigMenu.MenuEntry(option.getName(), () -> handler.accept(0), option::getDisplayValue);
            case BOOLEAN:
                return new ConfigMenu.CheckableEntry(option.getName(), () -> handler.accept(0), option::hasCheck, option::getDisplayValue);
            case NUMBER:
                return new ConfigMenu.SpinnerEntry(
                    option.getName(),
                    () -> handler.accept(Screen.hasShiftDown() ? -1 : 1),
                    () -> handler.accept(1),
                    () -> handler.accept(-1),
                    option::getDisplayValue
                );
            default:
                throw new AssertionError();
        }
    }

    private void spawnHoverText(Text text) {
        assert client != null;
        int mouseX = (int) (client.mouse.getX() * (double) client.getWindow().getScaledWidth() / client.getWindow().getWidth());
        int mouseY = (int) (client.mouse.getY() * (double) client.getWindow().getScaledHeight() / client.getWindow().getHeight());

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

    public void receiveTick() {
        menus.forEach(ConfigMenu::tick);
        menus.removeIf(ConfigMenu::isFullyClosed);

        children.clear();
        children.addAll(menus);

        hoverTexts.removeIf(txt -> txt.existTime-- < 0);
    }

    private int getTotalWidth(float partialTicks) {
        return menus.stream().mapToInt(menu -> menu.getDisplayableWidth(partialTicks)).sum();
    }

    public void receiveRender(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        assert client != null;

        matrices.push();
        matrices.translate(0, 0, 110);
        RenderSystem.disableDepthTest();

        int totalWidth = getTotalWidth(partialTicks);

        int oversize = Math.max(0, totalWidth - width);
        int leftOffset = -oversize;

        for (ConfigMenu menu : menus) {
            if (menu.canRender()) {
                int w = menu.getDisplayableWidth(partialTicks);
                menu.setHeight(height);
                menu.setLeftOffset(leftOffset);
                menu.render(matrices, mouseX, mouseY, partialTicks);
                leftOffset += w;
            }
        }

        matrices.translate(0, 0, 5);

        for (HoverText txt : hoverTexts) {
            float alpha = txt.existTime / 30f;
            int color = (int) (MathHelper.clamp(alpha, 0, 1) * 255) << 24;

            if ((color & 0xFC000000) != 0) {
                int width = client.textRenderer.getWidth(txt.text);
                drawHoverTextBackground(matrices, txt.x - width / 2 - 5, txt.y - 9, txt.x + width / 2 + 5, txt.y + 9, alpha);
                drawTextWithShadow(matrices, client.textRenderer, txt.text, txt.x - width / 2, txt.y - 4, color | 0xFFFFFF);
            }
        }

        RenderSystem.enableDepthTest();
        matrices.pop();
    }

    private void drawHoverTextBackground(MatrixStack matrices, int x1, int y1, int x2, int y2, float alpha) {
        float bgAlpha = alpha / 2;
        int color = (int) (MathHelper.clamp(alpha, 0, 1) * 255) << 24;
        int lighterColor = color | 0xAAAAAA;
        int darkerColor = color | 0x777777;
        int bgColor = (int) (MathHelper.clamp(bgAlpha, 0, 1) * 255) << 24;

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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            closeLastInteractiveMenu();
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (DebugClient.debugOptionsKey.matchesKey(keyCode, scanCode)) {
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
        MinecraftClient.getInstance().openScreen(INSTANCE);
    }

    private static class HoverText {
        private final Text text;
        private final int x, y;
        private int existTime = 30;

        private HoverText(Text text, int x, int y) {
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
        public void spawnResponse(Text response) {
            spawnHoverText(response);
        }

        @Override
        public void openMenu(DebugMenu menu) {
            DebugConfigScreen.this.openMenu(menu, index);
        }

        @Override
        public void copyToClipboard(String text) {
            assert client != null;
            clipboard.setClipboard(client.getWindow().getHandle(), text);
        }

        @Override
        public String getClipboard() {
            assert client != null;
            return clipboard.getClipboard(client.getWindow().getHandle(), null);
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
        public MinecraftClient client() {
            assert client != null;
            return client;
        }

        @Override
        public boolean screenPauses() {
            return pauses;
        }

        @Override
        public void setScreenPauses(boolean pause) {
            pauses = pause;
        }
    }
}
