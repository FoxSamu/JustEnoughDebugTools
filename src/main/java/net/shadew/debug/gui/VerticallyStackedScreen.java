package net.shadew.debug.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

import net.shadew.debug.api.menu.DebugMenuOverlayScreen;
import net.shadew.debug.gui.widgets.CompletableEditBox;
import net.shadew.debug.mixin.ScreenAccessor;

public abstract class VerticallyStackedScreen extends DebugMenuOverlayScreen {
    private final List<Pair<Alignable, IntSupplier>> alignables = new ArrayList<>();
    private final List<GuiEventListener> eventListeners = new ArrayList<>();
    private final List<AbstractWidget> widgets = new ArrayList<>();

    protected final CompletableEditBox.SuggestionsLayer suggestionsLayer = new CompletableEditBox.SuggestionsLayer(this);

    protected VerticallyStackedScreen(Component component, Screen parent) {
        super(component, parent);
    }

    protected void addComponent(Alignable alignable, IntSupplier height) {
        alignables.add(Pair.of(alignable, height));
    }

    protected void addListener(GuiEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    protected void addWidget(AbstractWidget widget) {
        addComponent(
            (centerX, y) -> {
                widget.x = centerX - widget.getWidth() / 2;
                widget.y = y;
            },
            widget::getHeight
        );
        addListener(widget);
        widgets.add(widget);
    }

    @Override
    protected void init() {
        super.init();

        ScreenAccessor accessor = ((ScreenAccessor) this);
        accessor.getRenderables().addAll(widgets);
        accessor.getChildren().addAll(eventListeners);

        int totalHeight = 0;
        for (Pair<Alignable, IntSupplier> pair : alignables)
            totalHeight += pair.getSecond().getAsInt() + 4;
        if (totalHeight > 0)
            totalHeight -= 4;
        int cx = width / 2;
        int cy = height / 2 - totalHeight / 2;
        for (Pair<Alignable, IntSupplier> pair : alignables) {
            pair.getFirst().align(cx, cy);
            cy += pair.getSecond().getAsInt() + 4;
        }
    }

    @Override
    public void mouseMoved(double mx, double my) {
        suggestionsLayer.mouseMoved(mx, my);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double delta) {
        if (suggestionsLayer.isMouseOver(mx, my) && suggestionsLayer.mouseScrolled(mx, my, delta))
            return true;
        return super.mouseScrolled(mx, my, delta);
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (suggestionsLayer.keyPressed(key, scan, mods))
            return true;
        return super.keyPressed(key, scan, mods);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (suggestionsLayer.mouseClicked(mx, my, button))
            return true;
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float tickProgress) {
        renderBackground(pose);
        super.render(pose, mouseX, mouseY, tickProgress);

        drawCenteredString(pose, font, getTitle(), width / 2, 20, 0xFFFFFFFF);

        suggestionsLayer.render(pose);
    }

    public interface Alignable {
        void align(int centerX, int y);
    }
}
