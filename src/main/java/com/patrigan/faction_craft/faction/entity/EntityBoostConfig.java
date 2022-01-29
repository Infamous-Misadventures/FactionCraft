package com.patrigan.faction_craft.faction.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.boost.Boosts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;

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

    public static EntityBoostConfig load(CompoundTag compoundNbt) {
        ListTag mandatory = compoundNbt.getList("mandatory", 10);
        ListTag whitelist = compoundNbt.getList("whitelist", 10);
        ListTag blacklist = compoundNbt.getList("blacklist", 10);
        ListTag rarityOverrides = compoundNbt.getList("rarityOverrides", 10);
        List<ResourceLocation> mandatoryList = mandatory.stream().map(inbt -> new ResourceLocation(((CompoundTag) inbt).getString("resourceLocation"))).collect(Collectors.toList());
        List<ResourceLocation> whitelistList = whitelist.stream().map(inbt -> new ResourceLocation(((CompoundTag) inbt).getString("resourceLocation"))).collect(Collectors.toList());
        List<ResourceLocation> blacklistList = blacklist.stream().map(inbt -> new ResourceLocation(((CompoundTag) inbt).getString("resourceLocation"))).collect(Collectors.toList());
        List<Pair<ResourceLocation, Boost.Rarity>> rarityOverridesList = rarityOverrides.stream().map(inbt -> {
            ResourceLocation resourceLocation = new ResourceLocation(((CompoundTag) inbt).getString("resourceLocation"));
            Boost.Rarity rarity = Boost.Rarity.byName(((CompoundTag) inbt).getString("rarity"), Boost.Rarity.NONE);
            return new Pair<>(resourceLocation, rarity);
        }).collect(Collectors.toList());
        return new EntityBoostConfig(
                mandatoryList,
                whitelistList,
                blacklistList,
                rarityOverridesList
        );
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

    public CompoundTag save(CompoundTag compoundNbt){
        ListTag mandatory = new ListTag();
        mandatory.addAll(mandatoryResourceLocations.stream().map(resourceLocation -> {
            CompoundTag listItemNbt = new CompoundTag();
            listItemNbt.putString("resourceLocation", resourceLocation.toString());
            return listItemNbt;
        }).collect(Collectors.toList()));
        compoundNbt.put("mandatory", mandatory);
        ListTag whitelist = new ListTag();
        whitelist.addAll(whitelistResourceLocations.stream().map(resourceLocation -> {
            CompoundTag listItemNbt = new CompoundTag();
            listItemNbt.putString("resourceLocation", resourceLocation.toString());
            return listItemNbt;
        }).collect(Collectors.toList()));
        compoundNbt.put("whitelist", whitelist);
        ListTag blacklist = new ListTag();
        blacklist.addAll(blacklistResourceLocations.stream().map(resourceLocation -> {
            CompoundTag listItemNbt = new CompoundTag();
            listItemNbt.putString("resourceLocation", resourceLocation.toString());
            return listItemNbt;
        }).collect(Collectors.toList()));
        compoundNbt.put("blacklist", blacklist);
        ListTag rarityOverrides = new ListTag();
        rarityOverrides.addAll(rarityOverridesLocations.stream().map(pair -> {
            CompoundTag listItemNbt = new CompoundTag();
            listItemNbt.putString("resourceLocation", pair.getFirst().toString());
            listItemNbt.putString("rarity", pair.getSecond().getName());
            return listItemNbt;
        }).collect(Collectors.toList()));
        compoundNbt.put("rarityOverrides", blacklist);
        return compoundNbt;
    }
}
