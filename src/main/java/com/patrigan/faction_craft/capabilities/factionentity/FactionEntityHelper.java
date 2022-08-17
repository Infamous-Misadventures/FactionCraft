package com.patrigan.faction_craft.capabilities.factionentity;

import net.minecraft.entity.MobEntity;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.factionentity.FactionEntityProvider.FACTION_ENTITY_CAPABILITY;

public class FactionEntityHelper {

    public static LazyOptional<IFactionEntity> getFactionEntityCapabilityLazy(MobEntity mobEntity)
    {
        if(FACTION_ENTITY_CAPABILITY == null) {
            return LazyOptional.empty();
        }
        LazyOptional<IFactionEntity> lazyCap = mobEntity.getCapability(FACTION_ENTITY_CAPABILITY);
        return lazyCap;
    }

    public static IFactionEntity getFactionEntityCapability(MobEntity mobEntity)
    {
        LazyOptional<IFactionEntity> lazyCap = mobEntity.getCapability(FACTION_ENTITY_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the RaidManager capability from the world!"));
        }
        return new FactionEntity(mobEntity);
    }
}
