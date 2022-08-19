package com.patrigan.faction_craft.capabilities.raider;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.RAIDER_CAPABILITY;


public class RaiderHelper {

    public static LazyOptional<Raider> getRaiderCapabilityLazy(Mob mobEntity)
    {
        if(RAIDER_CAPABILITY == null) {
            return LazyOptional.empty();
        }
        LazyOptional<Raider> lazyCap = mobEntity.getCapability(RAIDER_CAPABILITY);
        return lazyCap;
    }

    public static Raider getRaiderCapability(Mob mobEntity)
    {
        LazyOptional<Raider> lazyCap = mobEntity.getCapability(RAIDER_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the RaidManager capability from the level!"));
        }
        return null;
    }
}
