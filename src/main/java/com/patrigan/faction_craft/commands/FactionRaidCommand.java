package com.patrigan.faction_craft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.commands.arguments.FactionArgument;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.raid.Raid;
import com.patrigan.faction_craft.raid.target.PlayerRaidTarget;
import com.patrigan.faction_craft.raid.target.RaidTarget;
import com.patrigan.faction_craft.raid.target.VillageRaidTarget;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class FactionRaidCommand {
    private static final SimpleCommandExceptionType ERROR_START_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.raid.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> factionRaidCommand
                = Commands.literal("factionraid")
                .requires(commandSource -> commandSource.hasPermission(2))
                .then(Commands.literal("start").then(Commands.argument("faction", FactionArgument.factions()).then(Commands.literal("village").executes(sourceCommandContext ->
                    startVillageRaid(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction"))
                ).then(Commands.argument("location", BlockPosArgument.blockPos()).executes(sourceCommandContext ->
                    startVillageRaid(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction"), BlockPosArgument.getLoadedBlockPos(sourceCommandContext, "location"))
                )).then(Commands.argument("player", EntityArgument.player()).executes(sourceCommandContext ->
                    startVillageRaid(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction"), EntityArgument.getPlayer(sourceCommandContext, "player"))
                ))).then(Commands.literal("player").executes(sourceCommandContext ->
                    startPlayerRaid(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction"))
                ).then(Commands.argument("player", EntityArgument.player()).executes(sourceCommandContext ->
                    startPlayerRaid(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction"), EntityArgument.getPlayer(sourceCommandContext, "player"))
                )))))
                .then(Commands.literal("endwave").executes(sourceCommandContext ->
                        endRaidWave(sourceCommandContext.getSource())
                ).then(Commands.argument("location", BlockPosArgument.blockPos()).executes(sourceCommandContext ->
                        endRaidWave(sourceCommandContext.getSource(), BlockPosArgument.getLoadedBlockPos(sourceCommandContext, "location"))
                )).then(Commands.argument("player", EntityArgument.player()).executes(sourceCommandContext ->
                        endRaidWave(sourceCommandContext.getSource(), EntityArgument.getPlayer(sourceCommandContext, "player"))
                )))
                .then(Commands.literal("end").executes(sourceCommandContext ->
                        endRaid(sourceCommandContext.getSource())
                ).then(Commands.argument("location", BlockPosArgument.blockPos()).executes(sourceCommandContext ->
                        endRaid(sourceCommandContext.getSource(), BlockPosArgument.getLoadedBlockPos(sourceCommandContext, "location"))
                )).then(Commands.argument("player", EntityArgument.player()).executes(sourceCommandContext ->
                        endRaid(sourceCommandContext.getSource(), EntityArgument.getPlayer(sourceCommandContext, "player"))
                )));
        dispatcher.register(factionRaidCommand);
    }

    private static int endRaid(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        return endRaid(source, player.blockPosition());
    }

    private static int endRaid(CommandSourceStack source, ServerPlayer playerEntity) throws CommandSyntaxException {
        return endRaid(source, playerEntity.blockPosition());
    }

    private static int endRaid(CommandSourceStack source, BlockPos blockPos) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        return endRaid(source, level, blockPos);
    }

    private static int endRaidWave(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        return endRaidWave(source, player.blockPosition());
    }

    private static int endRaidWave(CommandSourceStack source, ServerPlayer playerEntity) throws CommandSyntaxException {
        return endRaidWave(source, playerEntity.blockPosition());
    }

    private static int endRaidWave(CommandSourceStack source, BlockPos blockPos) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        return endRaidWave(source, level, blockPos);
    }


    private static int startVillageRaid(CommandSourceStack source, Faction faction) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        return startVillageRaid(source, faction, player.blockPosition());
    }

    private static int startVillageRaid(CommandSourceStack source, Faction faction, ServerPlayer playerEntity) throws CommandSyntaxException {
        return startVillageRaid(source, faction, playerEntity.blockPosition());
    }

    private static int startVillageRaid(CommandSourceStack source, Faction faction, BlockPos blockPos) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        RaidTarget raidTarget = new VillageRaidTarget(blockPos, level);
        return createRaid(source, faction, level, raidTarget, blockPos.toString());
    }

    private static int startPlayerRaid(CommandSourceStack source, Faction faction) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        return startPlayerRaid(source, faction, player);
    }

    private static int startPlayerRaid(CommandSourceStack source, Faction faction, ServerPlayer playerEntity) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        RaidTarget raidTarget = new PlayerRaidTarget(playerEntity, level);
        return createRaid(source, faction, level, raidTarget, playerEntity.getDisplayName().getString());
    }

    private static int createRaid(CommandSourceStack source, Faction faction, ServerLevel level, RaidTarget raidTarget, String targetArgument) throws CommandSyntaxException {
        RaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(level);
        Raid raid = raidManagerCapability.createRaid(faction, raidTarget);
        if (raid == null) {
            throw ERROR_START_FAILED.create();
        } else {
            source.sendSuccess(new TranslatableComponent("commands.raid.success", targetArgument), true);
        }
        return 1;
    }

    private static int endRaidWave(CommandSourceStack source, ServerLevel level, BlockPos blockPos) throws CommandSyntaxException {
        RaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(level);
        Raid raid = raidManagerCapability.getRaidAt(blockPos);
        raid.endWave();
        if (raid == null) {
            throw ERROR_START_FAILED.create();
        } else {
            source.sendSuccess(new TranslatableComponent("commands.raid.success", blockPos.toString()), true);
        }
        return 1;
    }

    private static int endRaid(CommandSourceStack source, ServerLevel level, BlockPos blockPos) throws CommandSyntaxException {
        RaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(level);
        Raid raid = raidManagerCapability.getRaidAt(blockPos);
        raid.stop();
        if (raid == null) {
            throw ERROR_START_FAILED.create();
        } else {
            source.sendSuccess(new TranslatableComponent("commands.raid.success", blockPos.toString()), true);
        }
        return 1;
    }

}

