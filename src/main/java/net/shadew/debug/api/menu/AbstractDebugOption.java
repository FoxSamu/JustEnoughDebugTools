package net.shadew.debug.api.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import net.shadew.debug.api.status.DebugStatusKey;
import net.shadew.debug.api.status.ServerDebugStatus;

/**
 * An abstract and configurable implementation of {@link DebugOption}.
 *
 * @author Shadew
 * @see DebugOption
 * @since 0.1
 */
public abstract class AbstractDebugOption implements DebugOption {
    private final Component name;
    private Component longName;
    private Component description;
    private BooleanSupplier visible = () -> true;

    /**
     * @param name The name to display on the option widget
     * @since 0.1
     */
    public AbstractDebugOption(Component name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.1
     */
    @Override
    public Component getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.2
     */
    @Override
    public Component getLongName() {
        return longName;
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.2
     */
    @Override
    public Component getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.1
     */
    @Override
    public boolean isVisible() {
        return visible.getAsBoolean();
    }

    /**
     * Makes this option visible at any time.
     *
     * @since 0.1
     */
    public AbstractDebugOption show() {
        visible = () -> true;
        return this;
    }

    /**
     * Makes this option invisible at any time.
     *
     * @since 0.1
     */
    public AbstractDebugOption hide() {
        visible = () -> false;
        return this;
    }

    /**
     * Makes this option visible only if the given boolean supplier returns true.
     *
     * @param supp The lookup function.
     * @since 0.1
     */
    public AbstractDebugOption onlyIf(BooleanSupplier supp) {
        visible = supp;
        return this;
    }

    /**
     * Makes this option visible only if the given boolean supplier returns false.
     *
     * @param supp The lookup function.
     * @since 0.1
     */
    public AbstractDebugOption hideIf(BooleanSupplier supp) {
        visible = () -> !supp.getAsBoolean();
        return this;
    }

    /**
     * Makes this option visible only if the given {@link MutableBoolean} is true.
     *
     * @param bool The value
     * @since 0.1
     */
    public AbstractDebugOption onlyIf(MutableBoolean bool) {
        visible = bool::booleanValue;
        return this;
    }

    /**
     * Makes this option visible only if the given {@link MutableBoolean} is false.
     *
     * @param bool The value
     * @since 0.1
     */
    public AbstractDebugOption hideIf(MutableBoolean bool) {
        visible = () -> !bool.booleanValue();
        return this;
    }

    /**
     * Makes this option visible only if the server status has the given key available.
     *
     * @param status The server debug status instance
     * @param key    The status key that must be available
     * @since 0.1
     */
    public AbstractDebugOption onlyIf(ServerDebugStatus status, DebugStatusKey<?> key) {
        visible = () -> status.isAvailable(key);
        return this;
    }

    /**
     * Makes this option visible only if the server status has the given key unavailable.
     *
     * @param status The server debug status instance
     * @param key    The status key that must be available
     * @since 0.1
     */
    public AbstractDebugOption hideIf(ServerDebugStatus status, DebugStatusKey<?> key) {
        visible = () -> !status.isAvailable(key);
        return this;
    }

    /**
     * Makes this option visible only if the given {@link Minecraft} predicate returns true.
     *
     * @param pred The lookup function
     * @since 0.1
     */
    public AbstractDebugOption onlyIf(Predicate<Minecraft> pred) {
        visible = () -> pred.test(Minecraft.getInstance());
        return this;
    }

    /**
     * Makes this option visible only if the given {@link Minecraft} predicate returns false.
     *
     * @param pred The lookup function
     * @since 0.1
     */
    public AbstractDebugOption hideIf(Predicate<Minecraft> pred) {
        visible = () -> !pred.test(Minecraft.getInstance());
        return this;
    }

    /**
     * Sets the description of this component.
     *
     * @param desc The description
     * @since 0.2
     */
    public AbstractDebugOption desc(Component desc) {
        description = desc;
        return this;
    }

    /**
     * Sets the long name of this component.
     *
     * @param name The long name
     * @since 0.2
     */
    public AbstractDebugOption longName(Component name) {
        longName = name;
        return this;
    }
}
