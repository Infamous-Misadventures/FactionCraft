package com.patrigan.faction_craft.capabilities.appliedboosts;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AppliedBoostsProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IAppliedBoosts.class)
    public static final Capability<IAppliedBoosts> APPLIED_BOOSTS_CAPABILITY = null;

    private LazyOptional<IAppliedBoosts> instance = LazyOptional.of(APPLIED_BOOSTS_CAPABILITY::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == APPLIED_BOOSTS_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return APPLIED_BOOSTS_CAPABILITY.getStorage().writeNBT(APPLIED_BOOSTS_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        APPLIED_BOOSTS_CAPABILITY.getStorage().readNBT(APPLIED_BOOSTS_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
}