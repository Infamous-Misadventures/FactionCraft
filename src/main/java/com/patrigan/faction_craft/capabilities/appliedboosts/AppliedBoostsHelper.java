package com.patrigan.faction_craft.capabilities.appliedboosts;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.capabilities.appliedboosts.AppliedBoostsProvider.APPLIED_BOOSTS_CAPABILITY;
import static com.patrigan.faction_craft.capabilities.raider.RaiderProvider.RAIDER_CAPABILITY;

public class AppliedBoostsHelper {

    public static LazyOptional<IAppliedBoosts> getAppliedBoostsCapabilityLazy(LivingEntity livingEntity)
    {
        if(APPLIED_BOOSTS_CAPABILITY == null) {
            return LazyOptional.empty();
        }
        LazyOptional<IAppliedBoosts> lazyCap = livingEntity.getCapability(APPLIED_BOOSTS_CAPABILITY);
        return lazyCap;
    }

    public static IAppliedBoosts getAppliedBoostsCapability(LivingEntity livingEntity)
    {
        LazyOptional<IAppliedBoosts> lazyCap = livingEntity.getCapability(APPLIED_BOOSTS_CAPABILITY);
        if (lazyCap.isPresent()) {
            return lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the Applied Boosts capability from the entity!"));
        }
        return null;
    }
}
