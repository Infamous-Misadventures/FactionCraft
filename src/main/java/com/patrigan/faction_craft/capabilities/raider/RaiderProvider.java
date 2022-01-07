package com.patrigan.faction_craft.capabilities.raider;

import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RaiderProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IRaider.class)
    public static final Capability<IRaider> RAIDER_CAPABILITY = null;

    private LazyOptional<IRaider> instance = LazyOptional.of(RAIDER_CAPABILITY::getDefaultInstance);

    public RaiderProvider(MobEntity mobEntity) {
        this.instance = LazyOptional.of(() -> new Raider(mobEntity));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == RAIDER_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return RAIDER_CAPABILITY.getStorage().writeNBT(RAIDER_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        RAIDER_CAPABILITY.getStorage().readNBT(RAIDER_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
}