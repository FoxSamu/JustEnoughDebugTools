package net.shadew.debug.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.function.IntConsumer;

import net.shadew.debug.gui.widgets.RotationStepsSlider;

public class TestAllPopupScreen extends VerticallyStackedScreen {
    private static final TranslatableComponent TITLE = new TranslatableComponent("gui.debug.test_query.all");
    private static final TranslatableComponent RUN_BUTTON_TEXT = new TranslatableComponent("gui.debug.test_query.run");

    private RotationStepsSlider rotationSteps;
    private Button runButton;
    private Button cancelButton;

    public TestAllPopupScreen(Screen parentScreen, IntConsumer handler) {
        super(TITLE, parentScreen);

        minecraft = Minecraft.getInstance();
        font = minecraft.font;

        rotationSteps = new RotationStepsSlider(0, 0, 200, 20, 0);
        addWidget(rotationSteps);

        runButton = new Button(0, 0, 200, 20, RUN_BUTTON_TEXT, button -> {
            onClose();
            handler.accept(rotationSteps.getRotationSteps());
        });
        addWidget(runButton);

        cancelButton = new Button(0, 0, 200, 20, CommonComponents.GUI_CANCEL, button -> {
            onClose();
        });
        addWidget(cancelButton);
    }
}
