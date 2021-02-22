package net.shadew.debug.impl.menu;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import net.shadew.debug.api.menu.DebugMenu;
import net.shadew.debug.api.menu.DebugOption;

public class DebugMenuImpl implements DebugMenu {
    private final Text header;
    private final List<DebugOption> options = new ArrayList<>();

    public DebugMenuImpl(Text header) {
        this.header = header;
    }

    @Override
    public Text getHeader() {
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
