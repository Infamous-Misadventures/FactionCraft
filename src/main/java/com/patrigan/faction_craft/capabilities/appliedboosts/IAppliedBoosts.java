package com.patrigan.faction_craft.capabilities.appliedboosts;

import com.patrigan.faction_craft.boost.Boost;

import java.util.List;

public interface IAppliedBoosts {
    List<Boost> getAppliedBoosts();

    void addAppliedBoost(Boost appliedBoost);

    void setAppliedBoosts(List<Boost> appliedBoosts);

    List<Boost> getBoostsOfType(Boost.BoostType boostType);
}
