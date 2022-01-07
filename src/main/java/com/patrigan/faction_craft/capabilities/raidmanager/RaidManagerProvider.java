package com.patrigan.faction_craft.capabilities.raidmanager;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RaidManagerProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IRaidManager.class)
    public static final Capability<IRaidManager> RAID_MANAGER_CAPABILITY = null;

    private LazyOptional<IRaidManager> instance = LazyOptional.of(RAID_MANAGER_CAPABILITY::getDefaultInstance);

    public RaidManagerProvider(ServerWorld world) {
        this.instance = LazyOptional.of(() -> new RaidManager(world));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == RAID_MANAGER_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return RAID_MANAGER_CAPABILITY.getStorage().writeNBT(RAID_MANAGER_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        RAID_MANAGER_CAPABILITY.getStorage().readNBT(RAID_MANAGER_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
}