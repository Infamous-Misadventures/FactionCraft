package com.patrigan.faction_craft.capabilities.raidmanager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.RAID_MANAGER_CAPABILITY;


public class RaidManagerHelper {

    public static RaidManager getRaidManagerCapability(Level world)
    {
        LazyOptional<RaidManager> lazyCap = world.getCapability(RAID_MANAGER_CAPABILITY);
        return lazyCap.orElse(new RaidManager((ServerLevel) world));
    }
}
