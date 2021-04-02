package net.shadew.debug.api.menu;

import net.minecraft.network.chat.Component;

import java.util.stream.Stream;

public interface DebugMenu {
    Component getHeader();
    Stream<DebugOption> options();
    void addOption(DebugOption option);
}
