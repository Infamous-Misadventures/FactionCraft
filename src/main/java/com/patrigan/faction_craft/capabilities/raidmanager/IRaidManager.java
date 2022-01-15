package com.patrigan.faction_craft.capabilities.raidmanager;

import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.raid.Raid;
import com.patrigan.faction_craft.raid.target.RaidTarget;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.Map;

public interface IRaidManager {

    void tick();

    Map<Integer, Raid> getRaids();

    Raid getRaidAt(BlockPos blockPos);

    Raid getNearbyRaid(BlockPos blockPos, int distance);

    Raid createRaid(Faction faction, RaidTarget raidTarget);

    Raid createRaid(Collection<Faction> faction, RaidTarget raidTarget);

    Raid createBadOmenRaid(RaidTarget raidTarget, ServerPlayerEntity player);

    void load(CompoundNBT tag);

    CompoundNBT save(CompoundNBT pCompound);

}
