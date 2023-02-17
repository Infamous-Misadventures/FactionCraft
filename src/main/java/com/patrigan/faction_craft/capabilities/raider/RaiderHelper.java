package com.patrigan.faction_craft.capabilities.raider;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.RAIDER_CAPABILITY;


public class RaiderHelper {

    public static Raider getRaiderCapability(Mob mobEntity)
    {
        return mobEntity.getCapability(RAIDER_CAPABILITY).orElse(new Raider(mobEntity));
    }
}
