package net.shadew.debug.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.function.Consumer;

import net.shadew.debug.command.argument.TestFunctionNameArgument;
import net.shadew.debug.gui.widgets.CompletableEditBox;

public class TestImportPopupScreen extends VerticallyStackedScreen {
    private static final TranslatableComponent TITLE = new TranslatableComponent("gui.debug.test_query.import_function");
    private static final TranslatableComponent IMPORT_BUTTON_TEXT = new TranslatableComponent("gui.debug.test_query.import");

    private CompletableEditBox<?> editBox;
    private Button exportButton;
    private Button cancelButton;
    private boolean initialized;

    public TestImportPopupScreen(Screen parentScreen, Consumer<String> handler) {
        super(TITLE, parentScreen);

        minecraft = Minecraft.getInstance();
        font = minecraft.font;

        editBox = new CompletableEditBox<>(font, 0, 0, 200, 20, Component.nullToEmpty(""), suggestionsLayer, TestFunctionNameArgument.testFunctionNameArgument());
        addWidget(editBox);

        exportButton = new Button(0, 0, 200, 20, IMPORT_BUTTON_TEXT, button -> {
            onClose();
            handler.accept(editBox.getValue());
        });
        addWidget(exportButton);

        cancelButton = new Button(0, 0, 200, 20, CommonComponents.GUI_CANCEL, button -> {
            onClose();
        });
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
            children.add(suggestionsLayer);
            if (!initialized) {
                editBox.setFocus(true);
                setFocused(editBox);
            }
        }

        initialized = true;
    }
}
