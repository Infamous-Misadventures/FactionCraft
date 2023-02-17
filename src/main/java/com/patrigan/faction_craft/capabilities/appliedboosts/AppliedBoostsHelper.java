package com.patrigan.faction_craft.capabilities.appliedboosts;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.APPLIED_BOOSTS_CAPABILITY;

public class AppliedBoostsHelper {

    public static LazyOptional<AppliedBoosts> getAppliedBoostsCapabilityLazy(LivingEntity livingEntity)
    {
        if(APPLIED_BOOSTS_CAPABILITY == null) {
            return LazyOptional.empty();
        }
        LazyOptional<AppliedBoosts> lazyCap = livingEntity.getCapability(APPLIED_BOOSTS_CAPABILITY);
        return lazyCap;
    }

    public static AppliedBoosts getAppliedBoostsCapability(LivingEntity livingEntity)
    {
        return livingEntity.getCapability(APPLIED_BOOSTS_CAPABILITY).orElse(new AppliedBoosts());
    }
}
