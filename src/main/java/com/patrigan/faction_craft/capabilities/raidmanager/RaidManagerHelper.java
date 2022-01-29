package com.patrigan.faction_craft.capabilities.raidmanager;

import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.RAID_MANAGER_CAPABILITY;


public class RaidManagerHelper {

    public static LazyOptional<RaidManager> getRaidManagerCapabilityLazy(Level world)
    {
        if(RAID_MANAGER_CAPABILITY == null) {
            return LazyOptional.empty();
        }
        LazyOptional<RaidManager> lazyCap = world.getCapability(RAID_MANAGER_CAPABILITY);
        return lazyCap;
    }

    public static RaidManager getRaidManagerCapability(Level world)
    {
        LazyOptional<RaidManager> lazyCap = world.getCapability(RAID_MANAGER_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the RaidManager capability from the world!"));
        }
        return null;
    }
}
