package com.patrigan.faction_craft.capabilities.raidmanager;

import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerProvider.RAID_MANAGER_CAPABILITY;

public class RaidManagerHelper {

    public static LazyOptional<IRaidManager> getRaidManagerCapabilityLazy(World world)
    {
        if(RAID_MANAGER_CAPABILITY == null) {
            return LazyOptional.empty();
        }
        LazyOptional<IRaidManager> lazyCap = world.getCapability(RAID_MANAGER_CAPABILITY);
        return lazyCap;
    }

    public static IRaidManager getRaidManagerCapability(World world)
    {
        LazyOptional<IRaidManager> lazyCap = world.getCapability(RAID_MANAGER_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the RaidManager capability from the world!"));
        }
        return null;
    }
}
