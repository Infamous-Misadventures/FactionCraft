package com.patrigan.faction_craft.capabilities.factionentity;

import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.FACTION_ENTITY_CAPABILITY;


public class FactionEntityHelper {

    public static FactionEntity getFactionEntityCapability(Mob mobEntity)
    {
        return mobEntity.getCapability(FACTION_ENTITY_CAPABILITY).orElse(new FactionEntity(mobEntity));
    }
}
