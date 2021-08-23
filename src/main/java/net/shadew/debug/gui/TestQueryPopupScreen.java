package net.shadew.debug.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.gametest.framework.TestClassNameArgument;
import net.minecraft.gametest.framework.TestFunctionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.function.BiConsumer;

import net.shadew.debug.gui.widgets.CompletableEditBox;
import net.shadew.debug.gui.widgets.RotationStepsSlider;
import net.shadew.debug.mixin.ScreenAccessor;

public class TestQueryPopupScreen extends VerticallyStackedScreen {
    private static final TranslatableComponent RUN_BUTTON_TEXT = new TranslatableComponent("gui.jedt.test_query.run");

    private final Type type;
    private final BiConsumer<String, Integer> handler;
    private final CompletableEditBox.SuggestionsLayer suggestionsLayer = new CompletableEditBox.SuggestionsLayer(this);
    private CompletableEditBox<?> editBox;
    private Button runButton;
    private Button cancelButton;
    private RotationStepsSlider rotationSteps;
    private boolean initialized;

    public TestQueryPopupScreen(Screen parentScreen, Type type, BiConsumer<String, Integer> handler) {
        super(type.translation, parentScreen);
        this.type = type;
        this.handler = handler;

        if (type == Type.BATCH) {
            // TODO
            throw new UnsupportedOperationException("Not yet implemented");
        }

        minecraft = Minecraft.getInstance();
        font = minecraft.font;

        if (type.argumentType != null) {
            editBox = new CompletableEditBox<>(font, 0, 0, 200, 20, Component.nullToEmpty(""), suggestionsLayer, type.argumentType);
            addWidget(editBox);
        }

        if (type != Type.EXPORT_FUNCTION) {
            rotationSteps = new RotationStepsSlider(0, 0, 200, 20, 0);
            addWidget(rotationSteps);
        }

        runButton = new Button(0, 0, 200, 20, RUN_BUTTON_TEXT, button -> {
            onClose();
            handler.accept(editBox == null ? "" : editBox.getValue(), rotationSteps == null ? 0 : rotationSteps.getRotationSteps());
        });
        addWidget(runButton);

        cancelButton = new Button(0, 0, 200, 20, RUN_BUTTON_TEXT, button -> onClose());
        addWidget(cancelButton);
    }

    public Type getType() {
        return type;
    }

    @Override
    public void tick() {
        super.tick();
        if (runButton != null && editBox != null) {
            runButton.active = editBox.hasValidValue();
        }
    }

    @Override
    protected void init() {
        super.init();

        if (editBox != null) {
            ((ScreenAccessor) this).getChildren().add(suggestionsLayer);
            if (!initialized) {
                editBox.setFocus(true);
                setFocused(editBox);
            }
        }

        initialized = true;
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float tickProgress) {
        renderBackground(pose);
        super.render(pose, mouseX, mouseY, tickProgress);

        drawCenteredString(pose, font, getTitle(), width / 2, 20, 0xFFFFFFFF);

        if (editBox != null) {
            editBox.render(pose, mouseX, mouseY, tickProgress);
            suggestionsLayer.render(pose);
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

    public enum Type {
        FUNCTION("function", TestFunctionArgument.testFunctionArgument()),
        CLASS("class", TestClassNameArgument.testClassName()),
        ALL("all", null),
        EXPORT_FUNCTION("export_function", TestFunctionArgument.testFunctionArgument()),
        BATCH("batch", null);

        final String name;
        final TranslatableComponent translation;
        final ArgumentType<?> argumentType;

        Type(String name, ArgumentType<?> argumentType) {
            this.name = name;
            this.translation = new TranslatableComponent("gui.debug.test_query." + name);
            this.argumentType = argumentType;
        }
    }
}
