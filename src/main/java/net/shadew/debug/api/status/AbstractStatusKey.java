package net.shadew.debug.api.status;

public abstract class AbstractStatusKey<T> implements DebugStatusKey<T> {
    protected final String name;
    protected final String loggableName;
    protected final T unavailableValue;

    protected AbstractStatusKey(String name, String loggableName, T unavailableValue) {
        this.name = name;
        this.loggableName = loggableName;
        this.unavailableValue = unavailableValue;
    }

    @Override
    public T getUnavailableValue() {
        return unavailableValue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLoggableName() {
        return loggableName;
    }
}
