package com.patrigan.faction_craft.capabilities.factionentity;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.FACTION_ENTITY_CAPABILITY;


public class FactionEntityHelper {

    public static LazyOptional<FactionEntity> getFactionEntityCapabilityLazy(Mob mobEntity)
    {
        if(FACTION_ENTITY_CAPABILITY == null) {
            return LazyOptional.empty();
        }
        LazyOptional<FactionEntity> lazyCap = mobEntity.getCapability(FACTION_ENTITY_CAPABILITY);
        return lazyCap;
    }

    public static FactionEntity getFactionEntityCapability(Mob mobEntity)
    {
        LazyOptional<FactionEntity> lazyCap = mobEntity.getCapability(FACTION_ENTITY_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the Faction Entity capability from the level!"));
        }
        return new FactionEntity(mobEntity);
    }
}
