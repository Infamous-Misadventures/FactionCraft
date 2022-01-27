package com.patrigan.faction_craft.capabilities.factionentity;

import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.nbt.CompoundNBT;

//TODO: add the FactionEntityType to this.
public interface IFactionEntity {

    FactionEntityType getFactionEntityType();

    void setFactionEntityType(FactionEntityType factionEntityType);

    Faction getFaction();

    void setFaction(Faction faction);

    CompoundNBT save(CompoundNBT tag);

    void load(CompoundNBT tag);
}
