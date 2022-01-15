package com.patrigan.faction_craft.capabilities.factionentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;


public class FactionEntityStorage implements Capability.IStorage<IFactionEntity> {

    @Override
    public INBT writeNBT(Capability<IFactionEntity> capability, IFactionEntity instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag = instance.save(tag);
        return tag;
    }

    @Override
    public void readNBT(Capability<IFactionEntity> capability, IFactionEntity instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.load(tag);
    }
}