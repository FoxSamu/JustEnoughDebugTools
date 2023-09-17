package net.shadew.debug.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;

import net.shadew.debug.gui.widgets.RotationStepsSlider;

public class TestAllPopupScreen extends VerticallyStackedScreen {
    private static final Component TITLE = Component.translatable("gui.jedt.test_query.all");
    private static final Component RUN_BUTTON_TEXT = Component.translatable("gui.jedt.test_query.run");

    private RotationStepsSlider rotationSteps;
    private Button runButton;
    private Button cancelButton;

    public TestAllPopupScreen(Screen parentScreen, IntConsumer handler) {
        super(TITLE, parentScreen);

        minecraft = Minecraft.getInstance();
        font = minecraft.font;

        rotationSteps = new RotationStepsSlider(0, 0, 200, 20, 0);
        addWidget(rotationSteps);

        runButton = GuiUtil.button(0, 0, 200, 20, RUN_BUTTON_TEXT, button -> {
            onClose();
            handler.accept(rotationSteps.getRotationSteps());
        });
        addWidget(runButton);

        cancelButton = GuiUtil.button(0, 0, 200, 20, CommonComponents.GUI_CANCEL, button -> onClose());
        addWidget(cancelButton);
    }
}
