package com.patrigan.faction_craft.capabilities.factioninteraction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.factioninteraction.FactionInteractionProvider.FACTION_INTERACTION_CAPABILITY;

public class FactionInteractionHelper {

    public static LazyOptional<IFactionInteraction> getFactionInteractionCapabilityLazy(PlayerEntity player)
    {
        if(FACTION_INTERACTION_CAPABILITY == null) {
            return LazyOptional.empty();
        }
        LazyOptional<IFactionInteraction> lazyCap = player.getCapability(FACTION_INTERACTION_CAPABILITY);
        return lazyCap;
    }

    public static IFactionInteraction getFactionInteractionCapability(PlayerEntity player)
    {
        LazyOptional<IFactionInteraction> lazyCap = player.getCapability(FACTION_INTERACTION_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the FactionInteraction capability from the world!"));
        }
        return null;
    }
}
