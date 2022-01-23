package com.patrigan.faction_craft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.patrigan.faction_craft.commands.arguments.FactionArgument;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.world.spawner.PatrolSpawner;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class FactionPatrolCommand {
    private static final SimpleCommandExceptionType ERROR_START_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.patrol.failed"));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> factionRaidCommand
                = Commands.literal("factionpatrol")
                .requires((commandSource) -> commandSource.hasPermission(2))
                .then(Commands.argument("faction", FactionArgument.factions()).then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .executes((sourceCommandContext) -> spawnPatrol(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction"), BlockPosArgument.getLoadedBlockPos(sourceCommandContext, "pos"))))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes((sourceCommandContext) -> spawnPatrol(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction"), EntityArgument.getPlayer(sourceCommandContext, "player")))));

        dispatcher.register(factionRaidCommand);
    }

    private static int spawnPatrol(CommandSource source, Faction faction, ServerPlayerEntity player) throws CommandSyntaxException {
        return spawnPatrol(source, faction, player.blockPosition());
    }

    private static int spawnPatrol(CommandSource source, Faction faction, BlockPos blockPos) throws CommandSyntaxException {
        ServerWorld level = source.getLevel();
        int spawns = PatrolSpawner.spawnPatrol(level, level.getRandom(), faction, blockPos);
        if (spawns < 0) {
            throw ERROR_START_FAILED.create();
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.patrol.success", spawns, blockPos), true);
        }
        return 1;
    }

}

