package net.shadew.debug;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;

import java.util.Locale;

import net.shadew.debug.api.DebugMenuInitializer;
import net.shadew.debug.api.menu.*;
import net.shadew.debug.api.status.DebugStatusKey;
import net.shadew.debug.api.status.ServerDebugStatus;
import net.shadew.debug.api.status.StandardStatusKeys;
import net.shadew.debug.impl.menu.*;
import net.shadew.debug.render.DebugRenderers;

public class DefaultMenuInitializer implements DebugMenuInitializer {
    private final Minecraft minecraft = Minecraft.getInstance();

    @Override
    public void onInitializeDebugMenu(DebugMenu root, DebugMenuManager factory, ServerDebugStatus debugStatus) {
        DebugMenu commands = factory.getMenu(DebugMenu.COMMANDS);
        DebugMenu timeCommands = factory.getMenu(DebugMenu.TIME_COMMANDS);
        DebugMenu gamemodeCommands = factory.getMenu(DebugMenu.GAMEMODE_COMMANDS);
        DebugMenu weatherCommands = factory.getMenu(DebugMenu.WEATHER_COMMANDS);
        DebugMenu difficultyCommands = factory.getMenu(DebugMenu.DIFFICULTY_COMMANDS);
        DebugMenu tickSpeedCommands = factory.getMenu(DebugMenu.TICK_SPEED_COMMANDS);
        DebugMenu miscCommands = factory.getMenu(DebugMenu.MISC_COMMANDS);
        DebugMenu actions = factory.getMenu(DebugMenu.ACTIONS);
        DebugMenu copy = factory.getMenu(DebugMenu.COPY);
        DebugMenu display = factory.getMenu(DebugMenu.DISPLAY);

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

        Component timeResponse = text("debug:commands.time.response");
        timeCommands.addOption(new BooleanGameruleOption(text("debug:commands.time.enabled"), GameRules.RULE_DAYLIGHT).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        timeCommands.addOption(new CommandOption(text("debug:commands.time.day"), "time set day", timeResponse));
        timeCommands.addOption(new CommandOption(text("debug:commands.time.noon"), "time set noon", timeResponse));
        timeCommands.addOption(new CommandOption(text("debug:commands.time.night"), "time set night", timeResponse));
        timeCommands.addOption(new CommandOption(text("debug:commands.time.midnight"), "time set midnight", timeResponse));

        Component gamemodeResponse = text("debug:commands.gamemode.response");
        gamemodeCommands.addOption(new CommandOption(text("debug:commands.gamemode.creative"), "gamemode creative", gamemodeResponse));
        gamemodeCommands.addOption(new CommandOption(text("debug:commands.gamemode.survival"), "gamemode survival", gamemodeResponse));
        gamemodeCommands.addOption(new CommandOption(text("debug:commands.gamemode.adventure"), "gamemode adventure", gamemodeResponse));
        gamemodeCommands.addOption(new CommandOption(text("debug:commands.gamemode.spectator"), "gamemode spectator", gamemodeResponse));

        Component weatherResponse = text("debug:commands.weather.response");
        weatherCommands.addOption(new BooleanGameruleOption(text("debug:commands.weather.enabled"), GameRules.RULE_WEATHER_CYCLE).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        weatherCommands.addOption(new CommandOption(text("debug:commands.weather.clear"), "weather clear", weatherResponse));
        weatherCommands.addOption(new CommandOption(text("debug:commands.weather.rain"), "weather rain", weatherResponse));
        weatherCommands.addOption(new CommandOption(text("debug:commands.weather.thunder"), "weather thunder", weatherResponse));

        Component difficultyResponse = text("debug:commands.difficulty.response");
        difficultyCommands.addOption(new CommandOption(new TranslatableComponent("options.difficulty.peaceful"), "difficulty peaceful", difficultyResponse));
        difficultyCommands.addOption(new CommandOption(new TranslatableComponent("options.difficulty.easy"), "difficulty easy", difficultyResponse));
        difficultyCommands.addOption(new CommandOption(new TranslatableComponent("options.difficulty.normal"), "difficulty normal", difficultyResponse));
        difficultyCommands.addOption(new CommandOption(new TranslatableComponent("options.difficulty.hard"), "difficulty hard", difficultyResponse));

        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.insomnia"), GameRules.RULE_DOINSOMNIA).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.mob_spawning"), GameRules.RULE_DOMOBSPAWNING).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.block_drops"), GameRules.RULE_DOBLOCKDROPS).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.entity_drops"), GameRules.RULE_DOENTITYDROPS).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.command_output"), GameRules.RULE_COMMANDBLOCKOUTPUT).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.debug_stick"), "give @s minecraft:debug_stick", text("debug:commands.misc.debug_stick.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.command_block"), "give @s minecraft:command_block", text("debug:commands.misc.command_block.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.command_block_cart"), "give @s minecraft:command_block_minecart", text("debug:commands.misc.command_block_cart.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.structure_block"), "give @s minecraft:structure_block", text("debug:commands.misc.structure_block.response")));

        tickSpeedCommands.addOption(new NumberGameruleOption(text("debug:commands.tick_speed"), GameRules.RULE_RANDOMTICKING).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC));
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

        copy.addOption(new SimpleActionOption(text("debug:copy_tp"), this::copyTpLocation).hideIf(Minecraft::showOnlyReducedInfo));
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

    private static Component text(String optionId) {
        return new TranslatableComponent(Util.makeDescriptionId("debug.options", new ResourceLocation(optionId)));
    }

    private void reloadChunks(OptionSelectContext context) {
        context.spawnResponse(text("debug:reload_chunks.response"));
        minecraft.levelRenderer.allChanged();
    }

    private void clearChat(OptionSelectContext context) {
        if (minecraft.gui != null) {
            context.spawnResponse(text("debug:clear_chat.response"));
            minecraft.gui.getChat().clearMessages(false);
        }
    }

    private void reloadResources(OptionSelectContext context) {
        context.spawnResponse(text("debug:reload_resources.response"));
        minecraft.reloadResourcePacks();
    }

    private void copyTpLocation(OptionSelectContext context) {
        if (!minecraft.showOnlyReducedInfo()) {
            LocalPlayer player = minecraft.player;
            assert player != null;

            ClientPacketListener netHandler = player.connection;
            if (netHandler == null) {
                return;
            }

            context.spawnResponse(text("debug:copy_tp.response"));
            context.copyToClipboard(
                String.format(
                    Locale.ROOT,
                    "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f",
                    player.level.dimension().location(),
                    player.getX(), player.getY(), player.getZ(),
                    player.xRot, player.yRot
                )
            );
        }
    }

    private static AbstractDebugOption hideReduced(AbstractDebugOption option, ServerDebugStatus status, DebugStatusKey<?> key) {
        return option.hideIf(() -> Minecraft.getInstance().showOnlyReducedInfo() || !status.isAvailable(key));
    }
}
