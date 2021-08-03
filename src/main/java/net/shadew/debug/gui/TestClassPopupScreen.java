package net.shadew.debug.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.gametest.framework.TestClassNameArgument;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.function.BiConsumer;

import net.shadew.debug.gui.widgets.CompletableEditBox;
import net.shadew.debug.gui.widgets.RotationStepsSlider;
import net.shadew.debug.mixin.ScreenAccessor;

public class TestClassPopupScreen extends VerticallyStackedScreen {
    private static final TranslatableComponent TITLE = new TranslatableComponent("gui.debug.test_query.class");
    private static final TranslatableComponent RUN_BUTTON_TEXT = new TranslatableComponent("gui.debug.test_query.run");

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

        runButton = new Button(0, 0, 200, 20, RUN_BUTTON_TEXT, button -> {
            onClose();
            handler.accept(editBox.getValue(), rotationSteps.getRotationSteps());
        });
        addWidget(runButton);

        cancelButton = new Button(0, 0, 200, 20, CommonComponents.GUI_CANCEL, button -> onClose());
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
                editBox.setFocus(true);
                setFocused(editBox);
            }
        }

        initialized = true;
    }
}
