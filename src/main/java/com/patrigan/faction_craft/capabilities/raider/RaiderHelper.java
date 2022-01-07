package com.patrigan.faction_craft.capabilities.raider;

import net.minecraft.entity.MobEntity;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.raider.RaiderProvider.RAIDER_CAPABILITY;

public class RaiderHelper {

    public static LazyOptional<IRaider> getRaiderCapabilityLazy(MobEntity mobEntity)
    {
        if(RAIDER_CAPABILITY == null) {
            return LazyOptional.empty();
        }
        LazyOptional<IRaider> lazyCap = mobEntity.getCapability(RAIDER_CAPABILITY);
        return lazyCap;
    }

    public static IRaider getRaiderCapability(MobEntity mobEntity)
    {
        LazyOptional<IRaider> lazyCap = mobEntity.getCapability(RAIDER_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the RaidManager capability from the world!"));
        }
        return null;
    }
}
