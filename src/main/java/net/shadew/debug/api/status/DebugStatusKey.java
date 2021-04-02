package net.shadew.debug.api.status;

import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;

public interface DebugStatusKey<T> {
    String getName();

    T getUnavailableValue();
    boolean isAvailable(T value);

    T read(JsonElement element);
    T read(FriendlyByteBuf buf);
    void write(T value, FriendlyByteBuf buf);

    String getLoggableName();
    String getLoggableValue(T value);
}
