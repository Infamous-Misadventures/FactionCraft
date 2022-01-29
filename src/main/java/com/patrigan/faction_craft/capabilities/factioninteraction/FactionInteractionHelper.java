package com.patrigan.faction_craft.capabilities.factioninteraction;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.FACTION_INTERACTION_CAPABILITY;


public class FactionInteractionHelper {

    public static LazyOptional<FactionInteraction> getFactionInteractionCapabilityLazy(Player player)
    {
        if(FACTION_INTERACTION_CAPABILITY == null) {
            return LazyOptional.empty();
        }
        LazyOptional<FactionInteraction> lazyCap = player.getCapability(FACTION_INTERACTION_CAPABILITY);
        return lazyCap;
    }

    public static FactionInteraction getFactionInteractionCapability(Player player)
    {
        LazyOptional<FactionInteraction> lazyCap = player.getCapability(FACTION_INTERACTION_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the FactionInteraction capability from the world!"));
        }
        return null;
    }
}
