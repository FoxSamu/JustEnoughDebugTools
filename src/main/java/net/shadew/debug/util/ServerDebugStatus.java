package net.shadew.debug.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class ServerDebugStatus {
    public final MutableBoolean available = new MutableBoolean(false);
    public final MutableBoolean gameruleSync = new MutableBoolean(false);
    public final MutableBoolean pathfindingInfo = new MutableBoolean(false);
    public final MutableBoolean neighborUpdateInfo = new MutableBoolean(false);

    public void read(PacketByteBuf buf) {
        synchronized (this) {
            disableAll();
            available.setValue(buf.readBoolean());

            if (available.booleanValue()) {
                gameruleSync.setValue(buf.readBoolean());
                pathfindingInfo.setValue(buf.readBoolean());
                neighborUpdateInfo.setValue(buf.readBoolean());
            }
        }
    }

    public ServerDebugStatus disableAll() {
        return available(false)
                   .gameruleSync(false)
                   .pathfindingInfo(false)
                   .neighborUpdateInfo(false);
    }

    public ServerDebugStatus enableAll() {
        return available(true)
                   .gameruleSync(true)
                   .pathfindingInfo(true)
                   .neighborUpdateInfo(true);
    }

    public ServerDebugStatus available(boolean v) {
        available.setValue(v);
        return this;
    }

    public ServerDebugStatus gameruleSync(boolean v) {
        gameruleSync.setValue(v);
        return this;
    }

    public ServerDebugStatus pathfindingInfo(boolean v) {
        pathfindingInfo.setValue(v);
        return this;
    }

    public ServerDebugStatus neighborUpdateInfo(boolean v) {
        neighborUpdateInfo.setValue(v);
        return this;
    }

    public void write(PacketByteBuf buf) {
        synchronized (this) {
            buf.writeBoolean(available.booleanValue());
            if (available.booleanValue()) {
                buf.writeBoolean(gameruleSync.booleanValue());
                buf.writeBoolean(pathfindingInfo.booleanValue());
                buf.writeBoolean(neighborUpdateInfo.booleanValue());
            }
        }
    }

    private static String availability(MutableBoolean v) {
        return v.booleanValue() ? "available" : "unavailable";
    }

    public void log(Logger logger, Level level) {
        if (available.booleanValue()) {
            logger.log(level, "Server debug status: available");
            logger.log(level, "- Game rule sync: {}", availability(gameruleSync));
            logger.log(level, "- Pathfinding info: {}", availability(pathfindingInfo));
            logger.log(level, "- Neighbor update info: {}", availability(neighborUpdateInfo));
        } else {
            logger.log(level, "Server debug status: unavailable");
        }
    }
}
