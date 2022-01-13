package com.patrigan.faction_craft.capabilities.patroller;

import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PatrollerProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IPatroller.class)
    public static final Capability<IPatroller> PATROLLER_CAPABILITY = null;

    private LazyOptional<IPatroller> instance = LazyOptional.of(PATROLLER_CAPABILITY::getDefaultInstance);

    public PatrollerProvider(MobEntity mobEntity) {
        this.instance = LazyOptional.of(() -> new Patroller(mobEntity));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == PATROLLER_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return PATROLLER_CAPABILITY.getStorage().writeNBT(PATROLLER_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        PATROLLER_CAPABILITY.getStorage().readNBT(PATROLLER_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
}