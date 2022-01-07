package com.patrigan.faction_craft.capabilities.raider;

import com.patrigan.faction_craft.capabilities.raider.IRaider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;


public class RaiderStorage implements Capability.IStorage<IRaider> {

    @Override
    public INBT writeNBT(Capability<IRaider> capability, IRaider instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag = instance.save(tag);
        return tag;
    }

    @Override
    public void readNBT(Capability<IRaider> capability, IRaider instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.load(tag);
    }
}