package com.patrigan.faction_craft.capabilities.appliedboosts;


import com.patrigan.faction_craft.boost.Boost;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AppliedBoosts implements IAppliedBoosts {

    List<Boost> appliedBoosts = new ArrayList<>();

    @Override
    public List<Boost> getAppliedBoosts() {
        return appliedBoosts;
    }

    @Override
    public void addAppliedBoost(Boost appliedBoost) {
        appliedBoosts.add(appliedBoost);
    }

    @Override
    public void setAppliedBoosts(List<Boost> appliedBoosts) {
        this.appliedBoosts = appliedBoosts;
    }

    @Override
    public List<Boost> getBoostsOfType(Boost.BoostType boostType) {
        return appliedBoosts.stream().filter(boost -> boost.getType().equals(boostType)).collect(Collectors.toList());
    }
}
