package dev.runefox.jedt.impl.menu;

import dev.runefox.jedt.api.menu.Item;
import dev.runefox.jedt.api.menu.Menu;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MenuImpl implements Menu {
    private final Component header;
    private final List<Item> options = new ArrayList<>();

    public MenuImpl(Component header) {
        this.header = header;
    }

    @Override
    public Component getHeader() {
        return header;
    }

    @Override
    public Stream<Item> options() {
        return options.stream();
    }

    @Override
    public void addOption(Item option) {
        options.add(option);
    }

    public void clear() {
        options.clear();
    }
}
