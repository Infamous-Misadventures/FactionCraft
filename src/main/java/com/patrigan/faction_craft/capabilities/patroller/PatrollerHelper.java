package com.patrigan.faction_craft.capabilities.patroller;

import net.minecraft.entity.MobEntity;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.patroller.PatrollerProvider.PATROLLER_CAPABILITY;

public class PatrollerHelper {

    public static LazyOptional<IPatroller> getPatrollerCapabilityLazy(MobEntity mobEntity)
    {
        if(PATROLLER_CAPABILITY == null) {
            return LazyOptional.empty();
        }
        LazyOptional<IPatroller> lazyCap = mobEntity.getCapability(PATROLLER_CAPABILITY);
        return lazyCap;
    }

    public static IPatroller getPatrollerCapability(MobEntity mobEntity)
    {
        LazyOptional<IPatroller> lazyCap = mobEntity.getCapability(PATROLLER_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the RaidManager capability from the world!"));
        }
        return null;
    }
}
