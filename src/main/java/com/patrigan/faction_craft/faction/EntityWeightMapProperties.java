package com.patrigan.faction_craft.faction;

import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.List;

public class EntityWeightMapProperties {
    private int wave = 0;
    private int omen = 0;
    private List<FactionEntityType.FactionRank> allowedRanks = Arrays.asList(FactionEntityType.FactionRank.values());
    private Biome biome = null;

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
        return allowedRanks;
    }

    public EntityWeightMapProperties setAllowedRanks(List<FactionEntityType.FactionRank> allowedRanks) {
        this.allowedRanks = allowedRanks;
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

    public Biome getBiome() {
        return biome;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }
}
