package com.patrigan.faction_craft.capabilities.factionentity;


import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.Factions;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class FactionEntity implements IFactionEntity {

    private MobEntity entity;
    private Faction faction = null;

    public FactionEntity() {
        this.entity = null;
    }

    public FactionEntity(MobEntity entity) {
        this.entity = entity;
    }

    @Override
    public Faction getFaction() {
        return faction;
    }

    @Override
    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        if(faction != null) {
            tag.putString("Faction", faction.getName().toString());
        }
        return tag;
    }

    @Override
    public void load(CompoundNBT tag) {
        if(tag.contains("Faction")) {
            ResourceLocation factionName = new ResourceLocation(tag.getString("Faction"));
            if (Factions.factionExists(factionName)) {
                faction = Factions.getFaction(factionName);
            }
        }
    }

}
