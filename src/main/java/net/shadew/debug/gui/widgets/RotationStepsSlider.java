package net.shadew.debug.gui.widgets;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class RotationStepsSlider extends AbstractSliderButton {
    private int rot;

    public RotationStepsSlider(int x, int y, int w, int h, double value) {
        super(x, y, w, h, new TextComponent("SHADEWHASFAILEDTODOHISJOBBLAMEHIMNOW"), value);
        applyValue();
        updateMessage();
    }

    public int getRotationSteps() {
        return rot;
    }

    @Override
    protected void updateMessage() {
        setMessage(new TranslatableComponent("gui.jedt.test_query.rotation_steps", rot));
    }

    @Override
    protected void applyValue() {
        rot = (int) (value * 3);
        value = rot / 3d;
    }
}
