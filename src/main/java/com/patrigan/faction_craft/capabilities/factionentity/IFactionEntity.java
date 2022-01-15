package com.patrigan.faction_craft.capabilities.factionentity;

import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.nbt.CompoundNBT;

public interface IFactionEntity {

    Faction getFaction();

    void setFaction(Faction faction);

    CompoundNBT save(CompoundNBT tag);

    void load(CompoundNBT tag);
}
