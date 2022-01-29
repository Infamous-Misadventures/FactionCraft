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
                )))));
        dispatcher.register(factionRaidCommand);
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

}

