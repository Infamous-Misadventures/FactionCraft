package com.patrigan.faction_craft.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.boost.Boosts;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FactionBoostConfig {

    public static final FactionBoostConfig DEFAULT = new FactionBoostConfig(BoostDistributionType.RANDOM, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    public static final Codec<FactionBoostConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    BoostDistributionType.CODEC.optionalFieldOf("distribution", BoostDistributionType.RANDOM).forGetter(FactionBoostConfig::getBoostDistributionType),
                    ResourceLocation.CODEC.listOf().optionalFieldOf("mandatory", new ArrayList<>()).forGetter(FactionBoostConfig::getMandatoryResourceLocations),
                    ResourceLocation.CODEC.listOf().optionalFieldOf("whitelist", new ArrayList<>()).forGetter(FactionBoostConfig::getWhitelistResourceLocations),
                    ResourceLocation.CODEC.listOf().optionalFieldOf("blacklist", new ArrayList<>()).forGetter(FactionBoostConfig::getBlacklistResourceLocations)
            ).apply(builder, FactionBoostConfig::new));

    private final BoostDistributionType boostDistributionType;
    private final List<ResourceLocation> mandatoryResourceLocations;
    private final List<ResourceLocation> whitelistResourceLocations;
    private final List<ResourceLocation> blacklistResourceLocations;

    public FactionBoostConfig(BoostDistributionType boostDistributionType, List<ResourceLocation> mandatoryResourceLocations, List<ResourceLocation> whitelistResourceLocations, List<ResourceLocation> blacklistResourceLocations) {
        this.boostDistributionType = boostDistributionType;
        this.mandatoryResourceLocations = mandatoryResourceLocations;
        this.whitelistResourceLocations = whitelistResourceLocations;
        this.blacklistResourceLocations = blacklistResourceLocations;
    }

    public BoostDistributionType getBoostDistributionType() {
        return boostDistributionType;
    }

    public List<ResourceLocation> getMandatoryResourceLocations() {
        return mandatoryResourceLocations;
    }

    public List<Boost> getMandatoryBoosts(){
        return mandatoryResourceLocations.stream().map(Boosts::getBoost).collect(Collectors.toList());
    }

    public List<ResourceLocation> getWhitelistResourceLocations() {
        return whitelistResourceLocations;
    }

    public List<Boost> getWhitelistBoosts(){
        return whitelistResourceLocations.stream().map(Boosts::getBoost).collect(Collectors.toList());
    }

    public List<ResourceLocation> getBlacklistResourceLocations() {
        return blacklistResourceLocations;
    }

    public List<Boost> getBlacklistBoosts(){
        return blacklistResourceLocations.stream().map(Boosts::getBoost).collect(Collectors.toList());
    }

    public enum BoostDistributionType{
        RANDOM("random"),
        UNIFORM_ALL("uniform_all"),
        UNIFORM_TYPE("uniform_type"),
        //Not yet supported types:
        LEADER("leader"),
        STRONG_FAVOURED("strong_favoured"),
        WEAK_FAVOURED("weak_favoured");

        public static final Codec<BoostDistributionType> CODEC = Codec.STRING.flatComapMap(s -> FactionBoostConfig.BoostDistributionType.byName(s, null), d -> DataResult.success(d.getName()));

        private final String name;

        BoostDistributionType(String name) {
            this.name = name;
        }

        public static FactionBoostConfig.BoostDistributionType byName(String name, FactionBoostConfig.BoostDistributionType defaultType) {
            for(FactionBoostConfig.BoostDistributionType distributionType : values()) {
                if (distributionType.name.equals(name)) {
                    return distributionType;
                }
            }

            return defaultType;
        }

        public String getName() {
            return name;
        }
    }
}
