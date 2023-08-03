package com.patrigan.faction_craft.faction;

import com.patrigan.faction_craft.faction.entity.FactionEntityRank;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityWeightMapProperties {
    private int wave = 1;
    private int omen = 0;
    private List<FactionEntityRank> allowedRanks = new ArrayList<>(Arrays.asList(FactionEntityRank.values()));
    private BlockPos blockPos = null;
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

    public List<FactionEntityRank> getAllowedRanks() {
        return allowedRanks;
    }

    public EntityWeightMapProperties setAllowedRanks(List<FactionEntityRank> allowedRanks) {
        this.allowedRanks = new ArrayList<>(allowedRanks);
        return this;
    }

    public EntityWeightMapProperties addAllowedRank(FactionEntityRank rank) {
        this.allowedRanks.add(rank);
        return this;
    }

    public EntityWeightMapProperties removeAllowedRank(FactionEntityRank rank) {
        this.allowedRanks.remove(rank);
        return this;
    }

    public Biome getBiome() {
        return biome;
    }

    public EntityWeightMapProperties setBiome(Biome biome) {
        this.biome = biome;
        return this;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public EntityWeightMapProperties setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
        return this;
    }

}
