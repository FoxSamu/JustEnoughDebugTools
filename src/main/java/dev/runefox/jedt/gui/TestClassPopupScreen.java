package dev.runefox.jedt.gui;

import dev.runefox.jedt.gui.widgets.CompletableEditBox;
import dev.runefox.jedt.gui.widgets.RotationStepsSlider;
import dev.runefox.jedt.mixin.ScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.gametest.framework.TestClassNameArgument;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;

public class TestClassPopupScreen extends VerticallyStackedScreen {
    private static final Component TITLE = Component.translatable("gui.jedt.test_query.class");
    private static final Component RUN_BUTTON_TEXT = Component.translatable("gui.jedt.test_query.run");

    private CompletableEditBox<?> editBox;
    private RotationStepsSlider rotationSteps;
    private Button runButton;
    private Button cancelButton;
    private boolean initialized;

    public TestClassPopupScreen(Screen parentScreen, BiConsumer<String, Integer> handler) {
        super(TITLE, parentScreen);

        minecraft = Minecraft.getInstance();
        font = minecraft.font;

        editBox = new CompletableEditBox<>(font, 0, 0, 200, 20, Component.nullToEmpty(""), suggestionsLayer, TestClassNameArgument.testClassName());
        addWidget(editBox);

        rotationSteps = new RotationStepsSlider(0, 0, 200, 20, 0);
        addWidget(rotationSteps);

        runButton = GuiUtil.button(0, 0, 200, 20, RUN_BUTTON_TEXT, button -> {
            onClose();
            handler.accept(editBox.getValue(), rotationSteps.getRotationSteps());
        });
        addWidget(runButton);

        cancelButton = GuiUtil.button(0, 0, 200, 20, CommonComponents.GUI_CANCEL, button -> onClose());
        addWidget(cancelButton);
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
                editBox.setFocused(true);
                setFocused(editBox);
            }
        }

        initialized = true;
    }
}