package com.patrigan.faction_craft.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.boost.Boosts;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoostConfig {

    public static final BoostConfig DEFAULT = new BoostConfig(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    public static final Codec<BoostConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.listOf().optionalFieldOf("mandatory", new ArrayList<>()).forGetter(BoostConfig::getMandatoryResourceLocations),
                    ResourceLocation.CODEC.listOf().optionalFieldOf("whitelist", new ArrayList<>()).forGetter(BoostConfig::getWhitelistResourceLocations),
                    ResourceLocation.CODEC.listOf().optionalFieldOf("blacklist", new ArrayList<>()).forGetter(BoostConfig::getBlacklistResourceLocations)
            ).apply(builder, BoostConfig::new));

    private final List<ResourceLocation> mandatoryResourceLocations;
    private final List<ResourceLocation> whitelistResourceLocations;
    private final List<ResourceLocation> blacklistResourceLocations;

    public BoostConfig(List<ResourceLocation> mandatoryResourceLocations, List<ResourceLocation> whitelistResourceLocations, List<ResourceLocation> blacklistResourceLocations) {
        this.mandatoryResourceLocations = mandatoryResourceLocations;
        this.whitelistResourceLocations = whitelistResourceLocations;
        this.blacklistResourceLocations = blacklistResourceLocations;
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
}
