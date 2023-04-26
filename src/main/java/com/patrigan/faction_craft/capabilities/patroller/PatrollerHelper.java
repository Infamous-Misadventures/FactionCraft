package com.patrigan.faction_craft.capabilities.patroller;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.PATROLLER_CAPABILITY;


public class PatrollerHelper {

    public static Patroller getPatrollerCapability(Mob mobEntity)
    {
        return mobEntity.getCapability(PATROLLER_CAPABILITY).orElse(new Patroller(mobEntity));
    }

    public static float getPatrollerWalkSpeed(Mob mobEntity)
    {
        return getPatrollerCapability(mobEntity).isPatrolLeader() ? 0.595F : 0.7F;
    }
}
