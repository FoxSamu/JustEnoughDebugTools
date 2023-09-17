package dev.runefox.jedt.api.status;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;

public class SimpleStatusKey extends AbstractStatusKey<Boolean> {
    protected SimpleStatusKey(String name, String loggableName) {
        super(name, loggableName, false);
    }

    @Override
    public boolean isAvailable(Boolean value) {
        return value;
    }

    @Override
    public Boolean read(JsonElement element) {
        if(element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
        }
        return null;
    }

    @Override
    public Boolean read(FriendlyByteBuf buf) {
        return buf.readBoolean();
    }

    @Override
    public void write(Boolean value, FriendlyByteBuf buf) {
        buf.writeBoolean(value);
    }

    @Override
    public String getLoggableValue(Boolean value) {
        return value ? "available" : "unavailable";
    }
}
