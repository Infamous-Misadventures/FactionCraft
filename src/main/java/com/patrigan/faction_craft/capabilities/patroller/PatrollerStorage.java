package com.patrigan.faction_craft.capabilities.patroller;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;


public class PatrollerStorage implements Capability.IStorage<IPatroller> {

    @Override
    public INBT writeNBT(Capability<IPatroller> capability, IPatroller instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag = instance.save(tag);
        return tag;
    }

    @Override
    public void readNBT(Capability<IPatroller> capability, IPatroller instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.load(tag);
    }
}