package dev.runefox.jedt.api.status;

public interface ServerDebugStatus {
    boolean isDebugAvailable();

    <T> T getStatus(DebugStatusKey<T> key);
    <T> boolean isAvailable(DebugStatusKey<T> key);

    interface Builder {
        <T> void registerKey(DebugStatusKey<T> key, T defaultValue);
    }

    interface Mutable extends ServerDebugStatus {
        void setDebugAvailable(boolean available);
        void disableDebug();
        void enableDebug();

        <T> void setStatus(DebugStatusKey<T> key, T value);
        <T> void reset(DebugStatusKey<T> key);
        <T> void disable(DebugStatusKey<T> key);
    }
}
