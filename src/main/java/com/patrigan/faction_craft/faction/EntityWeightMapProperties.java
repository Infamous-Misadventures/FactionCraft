package com.patrigan.faction_craft.faction;

import com.patrigan.faction_craft.faction.entity.FactionEntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityWeightMapProperties {
    int wave = 0;
    int omen = 0;
    List<FactionEntityType.FactionRank> allowedRanks = new ArrayList<>(Arrays.asList(FactionEntityType.FactionRank.values()));

    public int getWave() {
        return wave;
    }

    public EntityWeightMapProperties setWave(int wave) {
        this.wave = wave;
        return this;
    }

    public int getOmen() {
        return omen;
    }

    public EntityWeightMapProperties setOmen(int omen) {
        this.omen = omen;
        return this;
    }

    public List<FactionEntityType.FactionRank> getAllowedRanks() {
        return new ArrayList<>(allowedRanks);
    }

    public EntityWeightMapProperties setAllowedRanks(List<FactionEntityType.FactionRank> allowedRanks) {
        this.allowedRanks = new ArrayList<>(allowedRanks);
        return this;
    }

    public EntityWeightMapProperties addAllowedRank(FactionEntityType.FactionRank rank) {
        this.allowedRanks.add(rank);
        return this;
    }

    public EntityWeightMapProperties removeAllowedRank(FactionEntityType.FactionRank rank) {
        this.allowedRanks.remove(rank);
        return this;
    }
}
