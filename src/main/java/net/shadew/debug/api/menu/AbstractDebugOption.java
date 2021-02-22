package net.shadew.debug.api.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import net.shadew.debug.DebugClient;
import net.shadew.debug.api.status.DebugStatusKey;
import net.shadew.debug.api.status.ServerDebugStatus;

public abstract class AbstractDebugOption implements DebugOption {
    private final Text name;
    private BooleanSupplier visible = () -> true;

    public AbstractDebugOption(Text name) {
        this.name = name;
    }

    @Override
    public Text getName() {
        return name;
    }

    @Override
    public boolean isVisible() {
        return visible.getAsBoolean();
    }

    public AbstractDebugOption show() {
        visible = () -> true;
        return this;
    }

    public AbstractDebugOption hide() {
        visible = () -> false;
        return this;
    }

    public AbstractDebugOption onlyIf(BooleanSupplier supp) {
        visible = supp;
        return this;
    }

    public AbstractDebugOption hideIf(BooleanSupplier supp) {
        visible = () -> !supp.getAsBoolean();
        return this;
    }

    public AbstractDebugOption onlyIf(MutableBoolean bool) {
        visible = bool::booleanValue;
        return this;
    }

    public AbstractDebugOption hideIf(MutableBoolean bool) {
        visible = () -> !bool.booleanValue();
        return this;
    }

    public AbstractDebugOption onlyIf(ServerDebugStatus status, DebugStatusKey<?> key) {
        visible = () -> status.isAvailable(key);
        return this;
    }

    public AbstractDebugOption hideIf(ServerDebugStatus status, DebugStatusKey<?> key) {
        visible = () -> !status.isAvailable(key);
        return this;
    }

    public AbstractDebugOption onlyIf(Predicate<MinecraftClient> pred) {
        visible = () -> pred.test(MinecraftClient.getInstance());
        return this;
    }

    public AbstractDebugOption hideIf(Predicate<MinecraftClient> pred) {
        visible = () -> !pred.test(MinecraftClient.getInstance());
        return this;
    }
}
