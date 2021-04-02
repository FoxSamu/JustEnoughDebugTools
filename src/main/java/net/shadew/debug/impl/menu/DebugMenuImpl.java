package net.shadew.debug.impl.menu;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import net.shadew.debug.api.menu.DebugMenu;
import net.shadew.debug.api.menu.DebugOption;

public class DebugMenuImpl implements DebugMenu {
    private final Component header;
    private final List<DebugOption> options = new ArrayList<>();

    public DebugMenuImpl(Component header) {
        this.header = header;
    }

    @Override
    public Component getHeader() {
        return header;
    }

    @Override
    public Stream<DebugOption> options() {
        return options.stream();
    }

    @Override
    public void addOption(DebugOption option) {
        options.add(option);
    }
}
