package com.patrigan.faction_craft.capabilities.factioninteraction;


import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.registry.Factions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.FACTION_ENTITY_CAPABILITY;

public class FactionInteraction implements INBTSerializable<CompoundTag> {

    List<Faction> badOmenFactions = new ArrayList<>();

    public List<Faction> getBadOmenFactions() {
        return badOmenFactions;
    }

    public void addBadOmenFaction(Faction faction) {
        badOmenFactions.add(faction);
    }

    public void clearBadOmenFactions() {
        badOmenFactions.clear();
    }

    @Override
    public CompoundTag serializeNBT() {
        if (FACTION_ENTITY_CAPABILITY == null) {
            return new CompoundTag();
        } else {
            CompoundTag tag = new CompoundTag();
            ListTag listnbt = new ListTag();

            for (Faction faction : this.badOmenFactions) {
                CompoundTag compoundnbt = new CompoundTag();
                compoundnbt.putString("Faction", faction.getName().toString());
                listnbt.add(compoundnbt);
            }

            tag.put("BadOmenFactions", listnbt);
            return tag;
        }
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ListTag listnbt = tag.getList("BadOmenFactions", 10);
        for(int i = 0; i < listnbt.size(); ++i) {
            CompoundTag compoundnbt = listnbt.getCompound(i);
            ResourceLocation factionName = new ResourceLocation(compoundnbt.getString("Faction"));
            if(Factions.factionExists(factionName)) {
                Faction faction = Factions.getFaction(factionName);
                this.badOmenFactions.add(faction);
            }
        }
    }
}
