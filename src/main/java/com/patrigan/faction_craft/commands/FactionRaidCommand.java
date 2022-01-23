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
import com.patrigan.faction_craft.raid.target.PlayerRaidTarget;
import com.patrigan.faction_craft.raid.target.RaidTarget;
import com.patrigan.faction_craft.raid.target.VillageRaidTarget;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class FactionRaidCommand {
    private static final SimpleCommandExceptionType ERROR_START_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.raid.failed"));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> factionRaidCommand
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

    private static int startVillageRaid(CommandSource source, Faction faction) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        return startVillageRaid(source, faction, player.blockPosition());
    }

    private static int startVillageRaid(CommandSource source, Faction faction, ServerPlayerEntity playerEntity) throws CommandSyntaxException {
        return startVillageRaid(source, faction, playerEntity.blockPosition());
    }

    private static int startVillageRaid(CommandSource source, Faction faction, BlockPos blockPos) throws CommandSyntaxException {
        ServerWorld level = source.getLevel();
        RaidTarget raidTarget = new VillageRaidTarget(blockPos, level);
        return createRaid(source, faction, level, raidTarget, blockPos.toString());
    }

    private static int startPlayerRaid(CommandSource source, Faction faction) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        return startPlayerRaid(source, faction, player);
    }

    private static int startPlayerRaid(CommandSource source, Faction faction, ServerPlayerEntity playerEntity) throws CommandSyntaxException {
        ServerWorld level = source.getLevel();
        RaidTarget raidTarget = new PlayerRaidTarget(playerEntity, level);
        return createRaid(source, faction, level, raidTarget, playerEntity.getDisplayName().getString());
    }

    private static int createRaid(CommandSource source, Faction faction, ServerWorld level, RaidTarget raidTarget, String targetArgument) throws CommandSyntaxException {
        IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(level);
        Raid raid = raidManagerCapability.createRaid(faction, raidTarget);
        if (raid == null) {
            throw ERROR_START_FAILED.create();
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.raid.success", targetArgument), true);
        }
        return 1;
    }

}

