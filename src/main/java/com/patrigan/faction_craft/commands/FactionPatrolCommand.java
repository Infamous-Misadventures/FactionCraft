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
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class FactionPatrolCommand {
    private static final SimpleCommandExceptionType ERROR_START_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.raid.failed"));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> factionRaidCommand
                = Commands.literal("factionpatrol")
                .requires((commandSource) -> commandSource.hasPermission(2))
                .then(Commands.argument("pos", Vec3Argument.vec3()).then(Commands.argument("faction", FactionArgument.factions())
                        .executes((sourceCommandContext) -> spawnPatrol(sourceCommandContext.getSource(), Vec3Argument.getVec3(sourceCommandContext, "pos"), FactionArgument.getFaction(sourceCommandContext, "faction")))));

        dispatcher.register(factionRaidCommand);
    }

    private static int spawnPatrol(CommandSource source, Vector3d vector3d, Faction faction) throws CommandSyntaxException {
        ServerWorld level = source.getLevel();
        int spawns = PatrolSpawner.spawnPatrol(level, level.getRandom(), faction, new BlockPos(vector3d));
        if (spawns > 0) {
            throw ERROR_START_FAILED.create();
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.patrol.success", new BlockPos(vector3d), spawns), true);
        }
        return 1;
    }

}

