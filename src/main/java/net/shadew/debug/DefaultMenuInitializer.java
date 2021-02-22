package net.shadew.debug;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.GameRules;

import java.util.Locale;

import net.shadew.debug.api.DebugMenuInitializer;
import net.shadew.debug.api.menu.*;
import net.shadew.debug.api.status.DebugStatusKey;
import net.shadew.debug.api.status.ServerDebugStatus;
import net.shadew.debug.api.status.StandardStatusKeys;
import net.shadew.debug.impl.menu.*;
import net.shadew.debug.render.DebugRenderers;

public class DefaultMenuInitializer implements DebugMenuInitializer {
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void onInitializeDebugMenu(DebugMenu root, DebugMenuManager factory, ServerDebugStatus debugStatus) {
        DebugMenu commands = factory.getMenu("debug:commands");
        DebugMenu timeCommands = factory.getMenu("debug:time_commands");
        DebugMenu gamemodeCommands = factory.getMenu("debug:gamemode_commands");
        DebugMenu weatherCommands = factory.getMenu("debug:weather_commands");
        DebugMenu difficultyCommands = factory.getMenu("debug:difficulty_commands");
        DebugMenu tickSpeedCommands = factory.getMenu("debug:tick_speed_commands");
        DebugMenu miscCommands = factory.getMenu("debug:misc_commands");
        DebugMenu actions = factory.getMenu("debug:actions");
        DebugMenu copy = factory.getMenu("debug:copy");
        DebugMenu display = factory.getMenu("debug:display");

        root.addOption(new MenuOption(commands));
        root.addOption(new MenuOption(actions));
        root.addOption(new MenuOption(copy));
        root.addOption(new MenuOption(display));

        commands.addOption(new MenuOption(timeCommands));
        commands.addOption(new MenuOption(gamemodeCommands));
        commands.addOption(new MenuOption(weatherCommands));
        commands.addOption(new MenuOption(difficultyCommands));
        commands.addOption(new MenuOption(tickSpeedCommands));
        commands.addOption(new MenuOption(miscCommands));

        Text timeResponse = text("debug:commands.time.response");
        timeCommands.addOption(new BooleanGameruleOption(text("debug:commands.time.enabled"), GameRules.DO_DAYLIGHT_CYCLE).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        timeCommands.addOption(new CommandOption(text("debug:commands.time.day"), "time set day", timeResponse));
        timeCommands.addOption(new CommandOption(text("debug:commands.time.noon"), "time set noon", timeResponse));
        timeCommands.addOption(new CommandOption(text("debug:commands.time.night"), "time set night", timeResponse));
        timeCommands.addOption(new CommandOption(text("debug:commands.time.midnight"), "time set midnight", timeResponse));

        Text gamemodeResponse = text("debug:commands.gamemode.response");
        gamemodeCommands.addOption(new CommandOption(text("debug:commands.gamemode.creative"), "gamemode creative", gamemodeResponse));
        gamemodeCommands.addOption(new CommandOption(text("debug:commands.gamemode.survival"), "gamemode survival", gamemodeResponse));
        gamemodeCommands.addOption(new CommandOption(text("debug:commands.gamemode.adventure"), "gamemode adventure", gamemodeResponse));
        gamemodeCommands.addOption(new CommandOption(text("debug:commands.gamemode.spectator"), "gamemode spectator", gamemodeResponse));

        Text weatherResponse = text("debug:commands.weather.response");
        weatherCommands.addOption(new BooleanGameruleOption(text("debug:commands.weather.enabled"), GameRules.DO_WEATHER_CYCLE).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        weatherCommands.addOption(new CommandOption(text("debug:commands.weather.clear"), "weather clear", weatherResponse));
        weatherCommands.addOption(new CommandOption(text("debug:commands.weather.rain"), "weather rain", weatherResponse));
        weatherCommands.addOption(new CommandOption(text("debug:commands.weather.thunder"), "weather thunder", weatherResponse));

        Text difficultyResponse = text("debug:commands.difficulty.response");
        difficultyCommands.addOption(new CommandOption(new TranslatableText("options.difficulty.peaceful"), "difficulty peaceful", difficultyResponse));
        difficultyCommands.addOption(new CommandOption(new TranslatableText("options.difficulty.easy"), "difficulty easy", difficultyResponse));
        difficultyCommands.addOption(new CommandOption(new TranslatableText("options.difficulty.normal"), "difficulty normal", difficultyResponse));
        difficultyCommands.addOption(new CommandOption(new TranslatableText("options.difficulty.hard"), "difficulty hard", difficultyResponse));

        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.insomnia"), GameRules.DO_INSOMNIA).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.mob_spawning"), GameRules.DO_MOB_SPAWNING).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.block_drops"), GameRules.DO_TILE_DROPS).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.entity_drops"), GameRules.DO_MOB_LOOT).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.command_output"), GameRules.COMMAND_BLOCK_OUTPUT).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.debug_stick"), "give @s minecraft:debug_stick", text("debug:commands.misc.debug_stick.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.command_block"), "give @s minecraft:command_block", text("debug:commands.misc.command_block.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.command_block_cart"), "give @s minecraft:command_block_minecart", text("debug:commands.misc.command_block_cart.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.structure_block"), "give @s minecraft:structure_block", text("debug:commands.misc.structure_block.response")));

        tickSpeedCommands.addOption(new NumberGameruleOption(text("debug:commands.tick_speed"), GameRules.RANDOM_TICK_SPEED).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        tickSpeedCommands.addOption(new CommandOption(text("debug:commands.tick_speed.0"), "gamerule randomTickSpeed 0", text("debug:commands.tick_speed.response.0")));
        tickSpeedCommands.addOption(new CommandOption(text("debug:commands.tick_speed.3"), "gamerule randomTickSpeed 3", text("debug:commands.tick_speed.response.3")));
        tickSpeedCommands.addOption(new CommandOption(text("debug:commands.tick_speed.10"), "gamerule randomTickSpeed 10", text("debug:commands.tick_speed.response.10")));
        tickSpeedCommands.addOption(new CommandOption(text("debug:commands.tick_speed.100"), "gamerule randomTickSpeed 100", text("debug:commands.tick_speed.response.100")));
        tickSpeedCommands.addOption(new CommandOption(text("debug:commands.tick_speed.1000"), "gamerule randomTickSpeed 1000", text("debug:commands.tick_speed.response.1000")));
        tickSpeedCommands.addOption(new CommandOption(text("debug:commands.tick_speed.10000"), "gamerule randomTickSpeed 10000", text("debug:commands.tick_speed.response.10000")));

        actions.addOption(new SimpleActionOption(text("debug:reload_resources"), this::reloadResources));
        actions.addOption(new CommandOption(text("debug:reload_datapacks"), "reload", text("debug:reload_datapacks.response")));
        actions.addOption(new SimpleActionOption(text("debug:reload_chunks"), this::reloadChunks));
        actions.addOption(new SimpleActionOption(text("debug:clear_chat"), this::clearChat));
        actions.addOption(new RenderDistanceOption(text("debug:render_distance")));
        actions.addOption(new LostFocusPauseOption(text("debug:pause_unfocus")));

        copy.addOption(new SimpleActionOption(text("debug:copy_tp"), this::copyTpLocation).hideIf(MinecraftClient::hasReducedDebugInfo));
        copy.addOption(new CopyTargetOption(text("debug:copy_targeted")));

        display.addOption(new EntityHitboxOption(text("debug:entity_hitboxes")));
        display.addOption(new ChunkBordersOption(text("debug:chunk_borders")));
        display.addOption(new AdvancedTooltipsOption(text("debug:advanced_tooltips")));
        display.addOption(hideReduced(new DebugRenderOption(text("debug:mob_paths"), DebugRenderers.PATHFINDING_ENABLED), debugStatus, StandardStatusKeys.SEND_PATHFINDING_INFO));
        display.addOption(hideReduced(new DebugRenderOption(text("debug:neighbor_updates"), DebugRenderers.NEIGHBOR_UPDATES_SHOWN), debugStatus, StandardStatusKeys.SEND_PATHFINDING_INFO));
        display.addOption(new DebugRenderOption(text("debug:heightmaps"), DebugRenderers.HEIGHTMAPS_SHOWN));
        display.addOption(new DebugRenderOption(text("debug:fluid_levels"), DebugRenderers.FLUID_LEVELS_SHOWN));
        display.addOption(new DebugRenderOption(text("debug:collisions"), DebugRenderers.COLLISIONS_SHOWN));
    }

    private static Text text(String optionId) {
        return new TranslatableText(Util.createTranslationKey("debug.options", new Identifier(optionId)));
    }

    private void reloadChunks(OptionSelectContext context) {
        context.spawnResponse(text("debug:reload_chunks.response"));
        client.worldRenderer.reload();
    }

    private void clearChat(OptionSelectContext context) {
        if (client.inGameHud != null) {
            context.spawnResponse(text("debug:clear_chat.response"));
            client.inGameHud.getChatHud().clear(false);
        }
    }

    private void reloadResources(OptionSelectContext context) {
        context.spawnResponse(text("debug:reload_resources.response"));
        client.reloadResources();
    }

    private void copyTpLocation(OptionSelectContext context) {
        if (!client.hasReducedDebugInfo()) {
            ClientPlayerEntity player = client.player;
            assert player != null;

            ClientPlayNetworkHandler netHandler = player.networkHandler;
            if (netHandler == null) {
                return;
            }

            context.spawnResponse(text("debug:copy_tp.response"));
            context.copyToClipboard(
                String.format(
                    Locale.ROOT,
                    "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f",
                    player.world.getRegistryKey().getValue(),
                    player.getX(), player.getY(), player.getZ(),
                    player.yaw, player.pitch
                )
            );
        }
    }

    private static AbstractDebugOption hideReduced(AbstractDebugOption option, ServerDebugStatus status, DebugStatusKey<?> key) {
        return option.hideIf(() -> MinecraftClient.getInstance().hasReducedDebugInfo() || !status.isAvailable(key));
    }
}
