package com.patrigan.faction_craft.capabilities.appliedboosts;


import com.patrigan.faction_craft.boost.Boost;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.APPLIED_BOOSTS_CAPABILITY;

public class AppliedBoosts implements INBTSerializable<CompoundTag> {

    List<Boost> appliedBoosts = new ArrayList<>();

    public List<Boost> getAppliedBoosts() {
        return appliedBoosts;
    }

    public void addAppliedBoost(Boost appliedBoost) {
        appliedBoosts.add(appliedBoost);
    }

    public void setAppliedBoosts(List<Boost> appliedBoosts) {
        this.appliedBoosts = appliedBoosts;
    }

    public List<Boost> getBoostsOfType(Boost.BoostType boostType) {
        return appliedBoosts.stream().filter(boost -> boost.getType().equals(boostType)).collect(Collectors.toList());
    }

    @Override
    public CompoundTag serializeNBT() {
        if (APPLIED_BOOSTS_CAPABILITY == null) {
            return new CompoundTag();
        } else {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            list.addAll(this.getAppliedBoosts().stream().map(boost -> boost.save(new CompoundTag())).collect(Collectors.toList()));
            tag.put("appliedBoosts", list);
            return tag;
        }
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if(APPLIED_BOOSTS_CAPABILITY != null) {
            ListTag appliedBoostsList = tag.getList("appliedBoosts", 10);
            this.setAppliedBoosts(appliedBoostsList.stream().map(inbt -> Boost.load((CompoundTag) inbt)).collect(Collectors.toList()));
        }
    }
}
