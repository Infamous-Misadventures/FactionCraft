package com.patrigan.faction_craft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.patrigan.faction_craft.commands.arguments.FactionArgument;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.level.spawner.PatrolSpawner;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class FactionPatrolCommand {
    private static final SimpleCommandExceptionType ERROR_START_FAILED = new SimpleCommandExceptionType(Component.translatable ("commands.patrol.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> factionRaidCommand
                = Commands.literal("factionpatrol")
                .requires(commandSource -> commandSource.hasPermission(2))
                .then(Commands.argument("faction", FactionArgument.factions()).executes(sourceCommandContext ->
                    spawnPatrol(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction"))
                ).then(Commands.argument("location", BlockPosArgument.blockPos()).executes(sourceCommandContext ->
                    spawnPatrol(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction"), BlockPosArgument.getLoadedBlockPos(sourceCommandContext, "location"))))
                .then(Commands.argument("player", EntityArgument.player()).executes(sourceCommandContext ->
                        spawnPatrol(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction"), EntityArgument.getPlayer(sourceCommandContext, "player")))));

        dispatcher.register(factionRaidCommand);
    }

    private static int spawnPatrol(CommandSourceStack source, Faction faction) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        return spawnPatrol(source, faction, player.blockPosition());
    }

    private static int spawnPatrol(CommandSourceStack source, Faction faction, ServerPlayer player) throws CommandSyntaxException {
        return spawnPatrol(source, faction, player.blockPosition());
    }

    private static int spawnPatrol(CommandSourceStack source, Faction faction, BlockPos blockPos) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        int spawns = PatrolSpawner.spawnPatrol(level, level.getRandom(), faction, blockPos);
        if (spawns < 0) {
            throw ERROR_START_FAILED.create();
        } else {
            source.sendSuccess(Component.translatable ("commands.patrol.success", spawns, blockPos), true);
        }
        return 1;
    }

}

