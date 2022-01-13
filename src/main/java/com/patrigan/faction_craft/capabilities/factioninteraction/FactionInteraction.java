package com.patrigan.faction_craft.capabilities.factioninteraction;


import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.Factions;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class FactionInteraction implements IFactionInteraction {

    List<Faction> badOmenFactions = new ArrayList<>();

    @Override
    public List<Faction> getBadOmenFactions() {
        return badOmenFactions;
    }

    @Override
    public void addBadOmenFaction(Faction faction) {
        badOmenFactions.add(faction);
    }

    @Override
    public void clearBadOmenFactions() {
        badOmenFactions.clear();
    }

    public void load(CompoundNBT tag) {
        ListNBT listnbt = tag.getList("BadOmenFactions", 10);
        for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            ResourceLocation factionName = new ResourceLocation(compoundnbt.getString("Faction"));
            if(Factions.factionExists(factionName)) {
                Faction faction = Factions.getFaction(factionName);
                this.badOmenFactions.add(faction);
            }
        }

    }

    public CompoundNBT save(CompoundNBT pCompound) {
        ListNBT listnbt = new ListNBT();

        for(Faction faction : this.badOmenFactions) {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("Faction", faction.getName().toString());
            listnbt.add(compoundnbt);
        }

        pCompound.put("BadOmenFactions", listnbt);
        return pCompound;
    }

}
