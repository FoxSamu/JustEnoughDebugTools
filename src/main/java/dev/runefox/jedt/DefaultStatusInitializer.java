package dev.runefox.jedt;

import dev.runefox.jedt.api.DebugStatusInitializer;
import dev.runefox.jedt.api.status.ServerDebugStatus;
import dev.runefox.jedt.api.status.StandardStatusKeys;

public class DefaultStatusInitializer implements DebugStatusInitializer {
    @Override
    public void onInitializeDebugStatus(ServerDebugStatus.Builder statusBuilder) {
        statusBuilder.registerKey(StandardStatusKeys.GAME_RULE_SYNC, true);
        statusBuilder.registerKey(StandardStatusKeys.SEND_PATHFINDING_INFO, true);
        statusBuilder.registerKey(StandardStatusKeys.SEND_NEIGHBOR_UPDATES, true);
        statusBuilder.registerKey(StandardStatusKeys.ALLOW_GAMETEST, true);
    }
}
