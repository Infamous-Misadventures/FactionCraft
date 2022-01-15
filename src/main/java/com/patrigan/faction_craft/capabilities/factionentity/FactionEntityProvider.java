package com.patrigan.faction_craft.capabilities.factionentity;

import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FactionEntityProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IFactionEntity.class)
    public static final Capability<IFactionEntity> FACTION_ENTITY_CAPABILITY = null;

    private LazyOptional<IFactionEntity> instance = LazyOptional.of(FACTION_ENTITY_CAPABILITY::getDefaultInstance);

    public FactionEntityProvider(MobEntity mobEntity) {
        this.instance = LazyOptional.of(() -> new FactionEntity(mobEntity));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == FACTION_ENTITY_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return FACTION_ENTITY_CAPABILITY.getStorage().writeNBT(FACTION_ENTITY_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        FACTION_ENTITY_CAPABILITY.getStorage().readNBT(FACTION_ENTITY_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
}