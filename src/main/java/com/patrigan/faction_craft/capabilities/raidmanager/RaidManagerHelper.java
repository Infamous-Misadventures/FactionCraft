package com.patrigan.faction_craft.capabilities.raidmanager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.RAID_MANAGER_CAPABILITY;


public class RaidManagerHelper {

    public static RaidManager getRaidManagerCapability(Level level)
    {
        return level.getCapability(RAID_MANAGER_CAPABILITY).orElse(new RaidManager((ServerLevel) level));
    }
}
