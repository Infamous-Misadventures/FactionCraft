package com.patrigan.faction_craft.capabilities.appliedboosts;

import com.patrigan.faction_craft.boost.Boost;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import java.util.stream.Collectors;


public class AppliedBoostsStorage implements Capability.IStorage<IAppliedBoosts> {

    @Override
    public INBT writeNBT(Capability<IAppliedBoosts> capability, IAppliedBoosts instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        ListNBT list = new ListNBT();
        list.addAll(instance.getAppliedBoosts().stream().map(boost -> boost.save(new CompoundNBT())).collect(Collectors.toList()));
        tag.put("appliedBoosts", list);
        return tag;
    }

    @Override
    public void readNBT(Capability<IAppliedBoosts> capability, IAppliedBoosts instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        ListNBT appliedBoosts = tag.getList("appliedBoosts", 10);
        instance.setAppliedBoosts(appliedBoosts.stream().map(inbt -> Boost.load((CompoundNBT) inbt)).collect(Collectors.toList()));
    }
}