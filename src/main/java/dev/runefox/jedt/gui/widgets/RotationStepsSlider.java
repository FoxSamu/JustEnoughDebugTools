package dev.runefox.jedt.gui.widgets;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class RotationStepsSlider extends AbstractSliderButton {
    private int rot;

    public RotationStepsSlider(int x, int y, int w, int h, double value) {
        super(x, y, w, h, Component.literal("SAMŪ HAS FAILED TO DO THEIR JOB BLAME THEM NOW"), value);
        applyValue();
        updateMessage();
    }

    public int getRotationSteps() {
        return rot;
    }

    @Override
    protected void updateMessage() {
        setMessage(Component.translatable("gui.jedt.test_query.rotation_steps", rot));
    }

    @Override
    protected void applyValue() {
        rot = (int) (value * 3);
        value = rot / 3d;
    }
}
