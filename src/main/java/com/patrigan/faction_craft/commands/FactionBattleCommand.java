package com.patrigan.faction_craft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.commands.arguments.FactionArgument;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.raid.Raid;
import com.patrigan.faction_craft.raid.target.FactionBattleRaidTarget;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.Arrays;

public class FactionBattleCommand {
    private static final SimpleCommandExceptionType ERROR_START_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.battle.failed"));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> factionRaidCommand
                = Commands.literal("factionbattle")
                .requires(commandSource -> commandSource.hasPermission(2))
                .then(Commands.argument("faction1", FactionArgument.factions()).then(Commands.argument("faction2", FactionArgument.factions()).executes(sourceCommandContext ->
                    spawnBattle(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction1"), FactionArgument.getFaction(sourceCommandContext, "faction2"))
                ).then(Commands.argument("location", BlockPosArgument.blockPos()).executes(sourceCommandContext ->
                    spawnBattle(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction1"), FactionArgument.getFaction(sourceCommandContext, "faction2"), BlockPosArgument.getLoadedBlockPos(sourceCommandContext, "location"))))
                .then(Commands.argument("player", EntityArgument.player()).executes(sourceCommandContext ->
                        spawnBattle(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction1"), FactionArgument.getFaction(sourceCommandContext, "faction2"), EntityArgument.getPlayer(sourceCommandContext, "player"))))));

        dispatcher.register(factionRaidCommand);
    }

    private static int spawnBattle(CommandSource source, Faction faction1, Faction faction2) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        return spawnBattle(source, faction1, faction2, player.blockPosition());
    }

    private static int spawnBattle(CommandSource source, Faction faction1, Faction faction2, ServerPlayerEntity player) throws CommandSyntaxException {
        return spawnBattle(source, faction1, faction2, player.blockPosition());
    }

    private static int spawnBattle(CommandSource source, Faction faction1, Faction faction2, BlockPos blockPos) throws CommandSyntaxException {
        ServerWorld level = source.getLevel();
        FactionBattleRaidTarget raidTarget = new FactionBattleRaidTarget(blockPos, faction1, faction2, level);
        IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(level);
        Raid raid = raidManagerCapability.createRaid(Arrays.asList(faction1, faction2), raidTarget);
        if (raid == null) {
            throw ERROR_START_FAILED.create();
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.battle.success", faction1.getName(), faction2.getName(), blockPos), true);
        }
        return 1;
    }

}

