package com.patrigan.faction_craft.capabilities.patroller;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.PATROLLER_CAPABILITY;


public class PatrollerHelper {

    public static LazyOptional<Patroller> getPatrollerCapabilityLazy(Mob mobEntity)
    {
        if(PATROLLER_CAPABILITY == null) {
            return LazyOptional.empty();
        }
        LazyOptional<Patroller> lazyCap = mobEntity.getCapability(PATROLLER_CAPABILITY);
        return lazyCap;
    }

    public static Patroller getPatrollerCapability(Mob mobEntity)
    {
        LazyOptional<Patroller> lazyCap = mobEntity.getCapability(PATROLLER_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the Patroller capability from the level!"));
        }
        return null;
    }
}
