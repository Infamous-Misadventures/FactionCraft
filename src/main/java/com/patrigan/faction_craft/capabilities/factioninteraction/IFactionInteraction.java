package com.patrigan.faction_craft.capabilities.factioninteraction;

import com.patrigan.faction_craft.faction.Faction;
import net.minecraft.nbt.CompoundNBT;

import java.util.List;

public interface IFactionInteraction {

    void addBadOmenFaction(Faction faction);

    List<Faction> getBadOmenFactions();

    void clearBadOmenFactions();

    void load(CompoundNBT tag);

    CompoundNBT save(CompoundNBT pCompound);
}
