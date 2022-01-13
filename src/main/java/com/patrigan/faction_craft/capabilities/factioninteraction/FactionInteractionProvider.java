package com.patrigan.faction_craft.capabilities.factioninteraction;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FactionInteractionProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IFactionInteraction.class)
    public static final Capability<IFactionInteraction> FACTION_INTERACTION_CAPABILITY = null;

    private LazyOptional<IFactionInteraction> instance = LazyOptional.of(FACTION_INTERACTION_CAPABILITY::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == FACTION_INTERACTION_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return FACTION_INTERACTION_CAPABILITY.getStorage().writeNBT(FACTION_INTERACTION_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        FACTION_INTERACTION_CAPABILITY.getStorage().readNBT(FACTION_INTERACTION_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }
}