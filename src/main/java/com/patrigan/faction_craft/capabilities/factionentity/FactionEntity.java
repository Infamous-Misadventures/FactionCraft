package com.patrigan.faction_craft.capabilities.factionentity;


import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.Factions;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class FactionEntity implements IFactionEntity {

    private MobEntity entity;
    private Faction faction = null;
    private FactionEntityType factionEntityType;

    public FactionEntity() {
        this.entity = null;
    }

    public FactionEntity(MobEntity entity) {
        this.entity = entity;
    }

    public MobEntity getEntity() {
        return entity;
    }

    public void setEntity(MobEntity entity) {
        this.entity = entity;
    }

    @Override
    public FactionEntityType getFactionEntityType() {
        return factionEntityType;
    }

    @Override
    public void setFactionEntityType(FactionEntityType factionEntityType) {
        this.factionEntityType = factionEntityType;
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
        if(factionEntityType != null) {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT = factionEntityType.save(compoundNBT);
            tag.put("FactionEntityType", compoundNBT);
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
        if(tag.contains("FactionEntityType")) {
            factionEntityType = FactionEntityType.load(tag.getCompound("FactionEntityType"));
        }
    }

}
