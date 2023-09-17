package dev.runefox.jedt.api.status;

public class StandardStatusKeys {
    public static final DebugStatusKey<Boolean> GAME_RULE_SYNC = new SimpleStatusKey("game_rule_sync", "Game rule sync");
    public static final DebugStatusKey<Boolean> SEND_PATHFINDING_INFO = new SimpleStatusKey("send_pathfinding_info", "Send pathfinding info");
    public static final DebugStatusKey<Boolean> SEND_NEIGHBOR_UPDATES = new SimpleStatusKey("send_neighbor_updates", "Send neighbor updates");
    public static final DebugStatusKey<Boolean> ALLOW_GAMETEST = new SimpleStatusKey("allow_gametest", "Allow GameTest");
}
