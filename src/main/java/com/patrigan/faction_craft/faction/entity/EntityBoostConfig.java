package com.patrigan.faction_craft.faction.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.boost.Boosts;
import com.patrigan.faction_craft.faction.FactionBoostConfig;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityBoostConfig {

    public static final EntityBoostConfig DEFAULT = new EntityBoostConfig(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    public static final Codec<EntityBoostConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.listOf().optionalFieldOf("mandatory", new ArrayList<>()).forGetter(EntityBoostConfig::getMandatoryResourceLocations),
                    ResourceLocation.CODEC.listOf().optionalFieldOf("whitelist", new ArrayList<>()).forGetter(EntityBoostConfig::getWhitelistResourceLocations),
                    ResourceLocation.CODEC.listOf().optionalFieldOf("blacklist", new ArrayList<>()).forGetter(EntityBoostConfig::getBlacklistResourceLocations),
                    Codec.mapPair(ResourceLocation.CODEC.fieldOf("boost"), Boost.Rarity.CODEC.fieldOf("rarity")).codec().listOf().optionalFieldOf("rarity_overrides", new ArrayList<>()).forGetter(EntityBoostConfig::getRarityOverridesLocations)
            ).apply(builder, EntityBoostConfig::new));

    private final List<ResourceLocation> mandatoryResourceLocations;
    private final List<ResourceLocation> whitelistResourceLocations;
    private final List<ResourceLocation> blacklistResourceLocations;
    private final List<Pair<ResourceLocation, Boost.Rarity>> rarityOverridesLocations;

    public EntityBoostConfig(List<ResourceLocation> mandatoryResourceLocations, List<ResourceLocation> whitelistResourceLocations, List<ResourceLocation> blacklistResourceLocations, List<Pair<ResourceLocation, Boost.Rarity>> rarityOverridesLocations) {
        this.mandatoryResourceLocations = mandatoryResourceLocations;
        this.whitelistResourceLocations = whitelistResourceLocations;
        this.blacklistResourceLocations = blacklistResourceLocations;
        this.rarityOverridesLocations = rarityOverridesLocations;
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

    public List<Pair<ResourceLocation, Boost.Rarity>> getRarityOverridesLocations() {
        return rarityOverridesLocations;
    }

    public Map<Boost, Boost.Rarity> getRarityOverrides() {
        return rarityOverridesLocations.stream().collect(Collectors.toMap(pair -> Boosts.getBoost(pair.getFirst()), Pair::getSecond));
    }
}
