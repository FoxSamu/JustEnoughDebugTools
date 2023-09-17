package dev.runefox.jedt.gui;

import dev.runefox.jedt.command.argument.TestFunctionNameArgument;
import dev.runefox.jedt.gui.widgets.CompletableEditBox;
import dev.runefox.jedt.mixin.ScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class TestExportPopupScreen extends VerticallyStackedScreen {
    private static final Component TITLE = Component.translatable("gui.jedt.test_query.export_function");
    private static final Component EXPORT_BUTTON_TEXT = Component.translatable("gui.jedt.test_query.export");

    private CompletableEditBox<?> editBox;
    private Button exportButton;
    private Button cancelButton;
    private boolean initialized;

    public TestExportPopupScreen(Screen parentScreen, Consumer<String> handler) {
        super(TITLE, parentScreen);

        minecraft = Minecraft.getInstance();
        font = minecraft.font;

        editBox = new CompletableEditBox<>(font, 0, 0, 200, 20, Component.nullToEmpty(""), suggestionsLayer, TestFunctionNameArgument.testFunctionNameArgument());
        addWidget(editBox);

        exportButton = GuiUtil.button(0, 0, 200, 20, EXPORT_BUTTON_TEXT, button -> {
            onClose();
            handler.accept(editBox.getValue());
        });
        addWidget(exportButton);

        cancelButton = GuiUtil.button(0, 0, 200, 20, CommonComponents.GUI_CANCEL, button -> onClose());
        addWidget(cancelButton);
    }

    @Override
    public void tick() {
        super.tick();

        if (exportButton != null && editBox != null) {
            exportButton.active = editBox.hasValidValue();
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
