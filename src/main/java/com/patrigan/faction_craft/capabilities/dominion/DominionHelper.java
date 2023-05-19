package com.patrigan.faction_craft.capabilities.dominion;

import net.minecraft.world.level.Level;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.DOMINION_CAPABILITY;


public class DominionHelper {

    public static Dominion getCapability(Level level)
    {
        return level.getCapability(DOMINION_CAPABILITY).orElse(new Dominion());
    }
}
