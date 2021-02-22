package net.shadew.debug.api.menu;

import net.minecraft.text.Text;

import java.util.stream.Stream;

public interface DebugMenu {
    Text getHeader();
    Stream<DebugOption> options();
    void addOption(DebugOption option);
}
