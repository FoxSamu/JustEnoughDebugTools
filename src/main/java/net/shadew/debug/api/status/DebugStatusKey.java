package net.shadew.debug.api.status;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketByteBuf;

public interface DebugStatusKey<T> {
    String getName();

    T getUnavailableValue();
    boolean isAvailable(T value);

    T read(JsonElement element);
    T read(PacketByteBuf buf);
    void write(T value, PacketByteBuf buf);

    String getLoggableName();
    String getLoggableValue(T value);
}
