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
import com.patrigan.faction_craft.raid.target.VillageRaidTarget;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class FactionRaidCommand {
    private static final SimpleCommandExceptionType ERROR_START_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.raid.failed"));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> factionRaidCommand
                = Commands.literal("factionraid")
                .requires((commandSource) -> commandSource.hasPermission(2))
                .then(Commands.literal("start").then(Commands.argument("faction", FactionArgument.factions())
                        .executes((sourceCommandContext) -> startRaid(sourceCommandContext.getSource(), FactionArgument.getFaction(sourceCommandContext, "faction")))));

        dispatcher.register(factionRaidCommand);
    }

    private static int startRaid(CommandSource source, Faction faction) throws CommandSyntaxException {

        ServerPlayerEntity player = source.getPlayerOrException();
        World level = player.level;
        IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(level);

        Raid raid = raidManagerCapability.createRaid(faction, new VillageRaidTarget(player.blockPosition(), (ServerWorld) level));
        if (raid == null) {
            throw ERROR_START_FAILED.create();
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.raid.success", player.blockPosition()), true);
        }
        return 1;
    }

}

