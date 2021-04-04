package net.shadew.debug;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;

import java.util.Locale;
import java.util.function.BooleanSupplier;

import net.shadew.debug.api.DebugMenuInitializer;
import net.shadew.debug.api.menu.*;
import net.shadew.debug.api.status.DebugStatusKey;
import net.shadew.debug.api.status.ServerDebugStatus;
import net.shadew.debug.api.status.StandardStatusKeys;
import net.shadew.debug.gui.*;
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
        DebugMenu gametest = factory.getMenu(DebugMenu.GAMETEST);

        root.addOption(new MenuOption(commands).desc(new TranslatableComponent("debug.menu.debug.commands.desc")));
        root.addOption(new MenuOption(actions).desc(new TranslatableComponent("debug.menu.debug.actions.desc")));
        root.addOption(new MenuOption(copy).desc(new TranslatableComponent("debug.menu.debug.copy.desc")));
        root.addOption(new MenuOption(display).desc(new TranslatableComponent("debug.menu.debug.display.desc")));
        root.addOption(new MenuOption(gametest).desc(new TranslatableComponent("debug.menu.debug.gametest.desc")));

        commands.addOption(new MenuOption(timeCommands).longName(new TranslatableComponent("debug.menu.debug.time_commands.long")));
        commands.addOption(new MenuOption(gamemodeCommands).longName(new TranslatableComponent("debug.menu.debug.gamemode_commands.long")));
        commands.addOption(new MenuOption(weatherCommands).longName(new TranslatableComponent("debug.menu.debug.weather_commands.long")));
        commands.addOption(new MenuOption(difficultyCommands).longName(new TranslatableComponent("debug.menu.debug.difficulty_commands.long")));
        commands.addOption(new MenuOption(tickSpeedCommands).longName(new TranslatableComponent("debug.menu.debug.tick_speed_commands.long")));
        commands.addOption(new MenuOption(miscCommands).longName(new TranslatableComponent("debug.menu.debug.misc_commands.long")));

        Component timeResponse = text("debug:commands.time.response");
        timeCommands.addOption(new BooleanGameruleOption(text("debug:commands.time.enabled"), GameRules.RULE_DAYLIGHT).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC).longName(longn("debug:commands.time.enabled")));
        timeCommands.addOption(new CommandOption(text("debug:commands.time.day"), "time set day", timeResponse).longName(longn("debug:commands.time.day")));
        timeCommands.addOption(new CommandOption(text("debug:commands.time.noon"), "time set noon", timeResponse).longName(longn("debug:commands.time.noon")));
        timeCommands.addOption(new CommandOption(text("debug:commands.time.night"), "time set night", timeResponse).longName(longn("debug:commands.time.night")));
        timeCommands.addOption(new CommandOption(text("debug:commands.time.midnight"), "time set midnight", timeResponse).longName(longn("debug:commands.time.midnight")));

        Component gamemodeResponse = text("debug:commands.gamemode.response");
        gamemodeCommands.addOption(new CommandOption(text("debug:commands.gamemode.creative"), "gamemode creative", gamemodeResponse).longName(longn("debug:commands.gamemode.creative")));
        gamemodeCommands.addOption(new CommandOption(text("debug:commands.gamemode.survival"), "gamemode survival", gamemodeResponse).longName(longn("debug:commands.gamemode.survival")));
        gamemodeCommands.addOption(new CommandOption(text("debug:commands.gamemode.adventure"), "gamemode adventure", gamemodeResponse).longName(longn("debug:commands.gamemode.adventure")));
        gamemodeCommands.addOption(new CommandOption(text("debug:commands.gamemode.spectator"), "gamemode spectator", gamemodeResponse).longName(longn("debug:commands.gamemode.spectator")));

        Component weatherResponse = text("debug:commands.weather.response");
        weatherCommands.addOption(new BooleanGameruleOption(text("debug:commands.weather.enabled"), GameRules.RULE_WEATHER_CYCLE).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC).longName(longn("debug:commands.weather.enabled")));
        weatherCommands.addOption(new CommandOption(text("debug:commands.weather.clear"), "weather clear", weatherResponse).longName(longn("debug:commands.weather.clear")));
        weatherCommands.addOption(new CommandOption(text("debug:commands.weather.rain"), "weather rain", weatherResponse).longName(longn("debug:commands.weather.rain")));
        weatherCommands.addOption(new CommandOption(text("debug:commands.weather.thunder"), "weather thunder", weatherResponse).longName(longn("debug:commands.weather.thunder")));

        Component difficultyResponse = text("debug:commands.difficulty.response");
        difficultyCommands.addOption(new CommandOption(new TranslatableComponent("options.difficulty.peaceful"), "difficulty peaceful", difficultyResponse).longName(longn("debug:commands.difficulty.peaceful")));
        difficultyCommands.addOption(new CommandOption(new TranslatableComponent("options.difficulty.easy"), "difficulty easy", difficultyResponse).longName(longn("debug:commands.difficulty.easy")));
        difficultyCommands.addOption(new CommandOption(new TranslatableComponent("options.difficulty.normal"), "difficulty normal", difficultyResponse).longName(longn("debug:commands.difficulty.normal")));
        difficultyCommands.addOption(new CommandOption(new TranslatableComponent("options.difficulty.hard"), "difficulty hard", difficultyResponse).longName(longn("debug:commands.difficulty.hard")));

        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.insomnia"), GameRules.RULE_DOINSOMNIA).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC).longName(longn("debug:commands.misc.insomnia")));
        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.mob_spawning"), GameRules.RULE_DOMOBSPAWNING).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC).longName(longn("debug:commands.misc.mob_spawning")));
        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.block_drops"), GameRules.RULE_DOBLOCKDROPS).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC).longName(longn("debug:commands.misc.block_drops")));
        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.entity_drops"), GameRules.RULE_DOENTITYDROPS).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC).longName(longn("debug:commands.misc.entity_drops")));
        miscCommands.addOption(new BooleanGameruleOption(text("debug:commands.misc.command_output"), GameRules.RULE_COMMANDBLOCKOUTPUT).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC).longName(longn("debug:commands.misc.command_output")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.debug_stick"), "give @s minecraft:debug_stick", text("debug:commands.misc.debug_stick.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.command_block"), "give @s minecraft:command_block", text("debug:commands.misc.command_block.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.command_block_cart"), "give @s minecraft:command_block_minecart", text("debug:commands.misc.command_block_cart.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.structure_block"), "give @s minecraft:structure_block", text("debug:commands.misc.structure_block.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.barrier"), "give @s minecraft:barrier", text("debug:commands.misc.barrier.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.light"), "give @s minecraft:light", text("debug:commands.misc.light.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.night_vision"), "effect give @s minecraft:night_vision 1000000 0 true", text("debug:commands.misc.night_vision.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.saturation"), "effect give @s minecraft:saturation 1000000 100 true", text("debug:commands.misc.saturation.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.regeneration"), "effect give @s minecraft:regeneration 1000000 100 true", text("debug:commands.misc.regeneration.response")));
        miscCommands.addOption(new CommandOption(text("debug:commands.misc.clear_effects"), "effect clear @s", text("debug:commands.misc.clear_effects.response")));

        tickSpeedCommands.addOption(new NumberGameruleOption(text("debug:commands.tick_speed"), GameRules.RULE_RANDOMTICKING).onlyIf(debugStatus, StandardStatusKeys.GAME_RULE_SYNC).longName(longn("debug:commands.tick_speed")));
        tickSpeedCommands.addOption(new CommandOption(text("debug:commands.tick_speed.0"), "gamerule randomTickSpeed 0", text("debug:commands.tick_speed.response.0")).longName(longn("debug:commands.tick_speed.0")));
        tickSpeedCommands.addOption(new CommandOption(text("debug:commands.tick_speed.3"), "gamerule randomTickSpeed 3", text("debug:commands.tick_speed.response.3")).longName(longn("debug:commands.tick_speed.3")));
        tickSpeedCommands.addOption(new CommandOption(text("debug:commands.tick_speed.10"), "gamerule randomTickSpeed 10", text("debug:commands.tick_speed.response.10")).longName(longn("debug:commands.tick_speed.10")));
        tickSpeedCommands.addOption(new CommandOption(text("debug:commands.tick_speed.100"), "gamerule randomTickSpeed 100", text("debug:commands.tick_speed.response.100")).longName(longn("debug:commands.tick_speed.100")));
        tickSpeedCommands.addOption(new CommandOption(text("debug:commands.tick_speed.1000"), "gamerule randomTickSpeed 1000", text("debug:commands.tick_speed.response.1000")).longName(longn("debug:commands.tick_speed.1000")));
        tickSpeedCommands.addOption(new CommandOption(text("debug:commands.tick_speed.10000"), "gamerule randomTickSpeed 10000", text("debug:commands.tick_speed.response.10000")).longName(longn("debug:commands.tick_speed.10000")));

        actions.addOption(new SimpleActionOption(text("debug:reload_resources"), this::reloadResources).desc(desc("debug:reload_resources")));
        actions.addOption(new CommandOption(text("debug:reload_datapacks"), "reload", text("debug:reload_datapacks.response")).desc(desc("debug:reload_datapacks")));
        actions.addOption(new SimpleActionOption(text("debug:reload_chunks"), this::reloadChunks).desc(desc("debug:reload_chunks")));
        actions.addOption(new SimpleActionOption(text("debug:clear_chat"), this::clearChat).desc(desc("debug:clear_chat")));
        actions.addOption(new RenderDistanceOption(text("debug:render_distance")).desc(desc("debug:render_distance")));
        actions.addOption(new LostFocusPauseOption(text("debug:pause_unfocus")).longName(longn("debug:pause_unfocus")).desc(desc("debug:pause_unfocus")));

        copy.addOption(new SimpleActionOption(text("debug:copy_tp"), this::copyTpLocation).hideIf(Minecraft::showOnlyReducedInfo).longName(longn("debug:copy_tp")).desc(desc("debug:copy_tp")));
        copy.addOption(new CopyTargetOption(text("debug:copy_targeted")).desc(desc("debug:copy_targeted")));

        display.addOption(new EntityHitboxOption(text("debug:entity_hitboxes")).desc(desc("debug:entity_hitboxes")));
        display.addOption(new ChunkBordersOption(text("debug:chunk_borders")).desc(desc("debug:chunk_borders")));
        display.addOption(new AdvancedTooltipsOption(text("debug:advanced_tooltips")).desc(desc("debug:advanced_tooltips")));
        display.addOption(hideReduced(new DebugRenderOption(text("debug:mob_paths"), DebugRenderers.PATHFINDING_ENABLED), debugStatus, StandardStatusKeys.SEND_PATHFINDING_INFO).desc(desc("debug:mob_paths")));
        display.addOption(hideReduced(new DebugRenderOption(text("debug:neighbor_updates"), DebugRenderers.NEIGHBOR_UPDATES_SHOWN), debugStatus, StandardStatusKeys.SEND_PATHFINDING_INFO).desc(desc("debug:neighbor_updates")));
        display.addOption(new DebugRenderOption(text("debug:heightmaps"), DebugRenderers.HEIGHTMAPS_SHOWN).desc(desc("debug:heightmaps")));
        display.addOption(new DebugRenderOption(text("debug:fluid_levels"), DebugRenderers.FLUID_LEVELS_SHOWN).desc(desc("debug:fluid_levels")));
        display.addOption(new DebugRenderOption(text("debug:collisions"), DebugRenderers.COLLISIONS_SHOWN).desc(desc("debug:collisions")));

        BooleanSupplier gametestEnabled = () -> DebugClient.serverDebugStatus.getStatus(StandardStatusKeys.ALLOW_GAMETEST);
        gametest.addOption(new CommandOption(text("debug:gametest.runthis"), "test runthis").closeScreenOnClick().onlyIf(gametestEnabled).desc(desc("debug:gametest.runthis")));
        gametest.addOption(new CommandOption(text("debug:gametest.runthese"), "test runthese").closeScreenOnClick().onlyIf(gametestEnabled).desc(desc("debug:gametest.runthese")));
        gametest.addOption(new DisplayScreenOption(text("debug:gametest.runall"), (component, parentScreen) -> createTestAllScreen(parentScreen)).closeScreenOnClick().onlyIf(gametestEnabled).desc(desc("debug:gametest.runall")));
        gametest.addOption(new DisplayScreenOption(text("debug:gametest.runfunction"), (component, parentScreen) -> createTestFunctionScreen(parentScreen)).closeScreenOnClick().onlyIf(gametestEnabled).desc(desc("debug:gametest.runfunction")));
        gametest.addOption(new DisplayScreenOption(text("debug:gametest.runclass"), (component, parentScreen) -> createTestClassScreen(parentScreen)).closeScreenOnClick().onlyIf(gametestEnabled).desc(desc("debug:gametest.runclass")));
        gametest.addOption(new CommandOption(text("debug:gametest.runfailed"), "test runfailed").closeScreenOnClick().onlyIf(gametestEnabled).desc(desc("debug:gametest.runfailed")));
        gametest.addOption(new CommandOption(text("debug:gametest.exportthis"), "test exportthis").closeScreenOnClick().onlyIf(gametestEnabled).desc(desc("debug:gametest.exportthis")));
        gametest.addOption(new DisplayScreenOption(text("debug:gametest.export"), (component, parentScreen) -> createTestExportScreen(parentScreen)).closeScreenOnClick().onlyIf(gametestEnabled).desc(desc("debug:gametest.export")));
        gametest.addOption(new DisplayScreenOption(text("debug:gametest.import"), (component, parentScreen) -> createTestImportScreen(parentScreen)).closeScreenOnClick().onlyIf(gametestEnabled).desc(desc("debug:gametest.import")));
        gametest.addOption(new CommandOption(text("debug:gametest.pos"), "test pos").closeScreenOnClick().onlyIf(gametestEnabled).desc(desc("debug:gametest.pos")));
        gametest.addOption(new CommandOption(text("debug:gametest.clearall"), "test clearall").closeScreenOnClick().onlyIf(gametestEnabled).desc(desc("debug:gametest.clearall")));
    }

    private static Screen createTestFunctionScreen(Screen parent) {
        return new TestFunctionPopupScreen(parent, (name, rs) -> {
            Minecraft mc = Minecraft.getInstance();
            assert mc.player != null;
            mc.player.chat("/test run " + name + " " + rs);
        });
    }

    private static Screen createTestClassScreen(Screen parent) {
        return new TestClassPopupScreen(parent, (name, rs) -> {
            Minecraft mc = Minecraft.getInstance();
            assert mc.player != null;
            mc.player.chat("/test runall " + name + " " + rs);
        });
    }

    private static Screen createTestAllScreen(Screen parent) {
        return new TestAllPopupScreen(parent, rs -> {
            Minecraft mc = Minecraft.getInstance();
            assert mc.player != null;
            mc.player.chat("/test runall " + rs);
        });
    }

    private static Screen createTestExportScreen(Screen parent) {
        return new TestExportPopupScreen(parent, name -> {
            Minecraft mc = Minecraft.getInstance();
            assert mc.player != null;
            mc.player.chat("/test export " + name);
        });
    }

    private static Screen createTestImportScreen(Screen parent) {
        return new TestImportPopupScreen(parent, name -> {
            Minecraft mc = Minecraft.getInstance();
            assert mc.player != null;
            mc.player.chat("/test import " + name);
        });
    }

    private static Component text(String optionId) {
        return new TranslatableComponent(Util.makeDescriptionId("debug.options", new ResourceLocation(optionId)));
    }

    private static Component desc(String optionId) {
        return new TranslatableComponent(Util.makeDescriptionId("debug.options", new ResourceLocation(optionId)) + ".desc");
    }

    private static Component longn(String optionId) {
        return new TranslatableComponent(Util.makeDescriptionId("debug.options", new ResourceLocation(optionId)) + ".long");
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
