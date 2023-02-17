package com.patrigan.faction_craft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.commands.arguments.FactionArgument;
import com.patrigan.faction_craft.commands.arguments.FactionEntitySummonArgument;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.FactionBoostHelper;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FactionSummonCommand {
    private static final SimpleCommandExceptionType ERROR_START_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.factionsummon.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("factionsummon").requires((p_138819_) -> {
            return p_138819_.hasPermission(2);
        }).then(Commands.argument("faction", FactionArgument.factions())
                .then(Commands.argument("entity", FactionEntitySummonArgument.id()).executes((p_138832_) -> {
                    return spawnEntity(p_138832_.getSource(), FactionArgument.getFaction(p_138832_, "faction"), FactionEntitySummonArgument.getSummonableEntity(p_138832_, "entity"), p_138832_.getSource().getPosition(), 0);
                }).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_138830_) -> {
                    return spawnEntity(p_138830_.getSource(), FactionArgument.getFaction(p_138830_, "faction"),  FactionEntitySummonArgument.getSummonableEntity(p_138830_, "entity"), Vec3Argument.getVec3(p_138830_, "pos"), 0);
                }).then(Commands.argument("boostStrength", IntegerArgumentType.integer(0)).executes((p_138817_) -> {
                    return spawnEntity(p_138817_.getSource(), FactionArgument.getFaction(p_138817_, "faction"),  FactionEntitySummonArgument.getSummonableEntity(p_138817_, "entity"), Vec3Argument.getVec3(p_138817_, "pos"), IntegerArgumentType.getInteger(p_138817_, "boostStrength"));
                }))))));
    }

    private static int spawnEntity(CommandSourceStack source, Faction faction, FactionEntityType factionEntityType, Vec3 pos, int boostStrength) {
        Entity entity = factionEntityType.createEntity(source.getLevel(), faction, new BlockPos(pos), false, MobSpawnType.PATROL);
        if(entity instanceof Mob mob) {
            FactionEntity entityCapability = FactionEntityHelper.getFactionEntityCapability(mob);
            entityCapability.getFaction().getBoostConfig().getMandatoryBoosts().forEach(boost -> boost.apply(mob));
            entityCapability.getFactionEntityType().getBoostConfig().getMandatoryBoosts().forEach(boost -> boost.apply(mob));
            if(boostStrength > 0) {
                FactionBoostHelper.applyBoosts(boostStrength, List.of(mob), faction, source.getLevel());
            }
        }
        source.sendSuccess(Component.translatable ("commands.factionsummon.success", pos.toString()), true);
        return 0;
    }


}

