package com.patrigan.faction_craft.capabilities.factioninteraction;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;


public class FactionInteractionStorage implements Capability.IStorage<IFactionInteraction> {

    public static final String FACTION_INTERACTION_KEY = "FactionInteraction";

    @Override
    public INBT writeNBT(Capability<IFactionInteraction> capability, IFactionInteraction instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag = instance.save(tag);
        return tag;
    }

    @Override
    public void readNBT(Capability<IFactionInteraction> capability, IFactionInteraction instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.load(tag);
    }
}