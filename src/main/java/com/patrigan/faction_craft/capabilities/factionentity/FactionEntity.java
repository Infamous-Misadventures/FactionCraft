package com.patrigan.faction_craft.capabilities.factionentity;


import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.registry.Factions;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Objects;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.FACTION_ENTITY_CAPABILITY;

public class FactionEntity implements INBTSerializable<CompoundTag> {

    private LivingEntity entity;
    private Faction faction = Faction.GAIA;
    private FactionEntityType factionEntityType;
    private BlockPos targetPosition = null;
    private LivingEntity nearestDamagedFactionAlly;
    private boolean isStuck = false;

    public FactionEntity() {
        this.entity = null;
    }

    public FactionEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public FactionEntityType getFactionEntityType() {
        return factionEntityType;
    }

    public void setFactionEntityType(FactionEntityType factionEntityType) {
        this.factionEntityType = factionEntityType;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = Objects.requireNonNullElse(faction, Faction.GAIA);
    }

    public BlockPos getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(BlockPos targetPosition) {
        this.targetPosition = targetPosition;
    }

    public boolean isStuck() {
        return isStuck;
    }

    public void setStuck(boolean stuck) {
        isStuck = stuck;
    }

    @Override
    public CompoundTag serializeNBT() {
        if (FACTION_ENTITY_CAPABILITY == null) {
            return new CompoundTag();
        } else {
            CompoundTag tag = new CompoundTag();
            if (faction != null) {
                tag.putString("Faction", faction.getName().toString());
            }
            if (factionEntityType != null) {
                CompoundTag compoundNBT = new CompoundTag();
                compoundNBT = factionEntityType.save(compoundNBT);
                tag.put("FactionEntityType", compoundNBT);
            }
            if(targetPosition != null) {
                tag.putLong("TargetPosition", targetPosition.asLong());
            }
            return tag;
        }
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if(tag.contains("Faction")) {
            ResourceLocation factionName = new ResourceLocation(tag.getString("Faction"));
            if (Factions.factionExists(factionName)) {
                faction = Factions.getFaction(factionName);
            }
        }
        if(tag.contains("FactionEntityType")) {
            factionEntityType = FactionEntityType.load(tag.getCompound("FactionEntityType"));
        }
        if(tag.contains("TargetPosition")) {
            targetPosition = BlockPos.of(tag.getLong("TargetPosition"));
        }
    }

    public LivingEntity getNearestDamagedFactionAlly() {
        return nearestDamagedFactionAlly;
    }

    public void setNearestDamagedFactionAlly(LivingEntity nearestDamagedFactionAlly) {
        this.nearestDamagedFactionAlly = nearestDamagedFactionAlly;
    }

    public boolean hasFaction() {
        return faction != null && faction != Faction.GAIA;
    }
}
