package com.patrigan.faction_craft.registry;

import com.mojang.datafixers.util.Pair;
import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.data.ResourceSet;
import com.patrigan.faction_craft.data.util.MergeableCodecDataManager;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.FactionBoostConfig;
import com.patrigan.faction_craft.faction.FactionRaidConfig;
import com.patrigan.faction_craft.faction.FactionRelations;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import com.patrigan.faction_craft.util.GeneralUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.patrigan.faction_craft.FactionCraft.LOGGER;
import static com.patrigan.faction_craft.config.FactionCraftConfig.DISABLED_FACTIONS;

public class Factions {

    public static final MergeableCodecDataManager<Faction, Faction> FACTION_DATA = new MergeableCodecDataManager<>("faction", Faction.CODEC, Factions::factionMerger);

    public static Faction factionMerger(List<Faction> raws, ResourceLocation id){
        ResourceLocation name = null;
        CompoundTag banner = null;
        FactionRaidConfig factionRaidConfig = null;
        FactionBoostConfig boostConfig = null;
        FactionRelations factionRelations = null;
        Set<FactionEntityType> entities = new HashSet<>();
        ResourceLocation activationAdvancement = null;
        ResourceSet<EntityType<?>> defaultEntities = new ResourceSet<>(Registry.ENTITY_TYPE_REGISTRY, new ArrayList<>());
        for (Faction raw : raws) {
            if (raw.isReplace()) {
                banner = raw.getBanner();
                name = raw.getName();
                factionRaidConfig = raw.getRaidConfig();
                boostConfig = null;
                factionRelations = null;
                entities = new HashSet<>();
                activationAdvancement = raw.getActivationAdvancement();
            }
            if(banner == null){
                banner = raw.getBanner();
            }
            if(name == null){
                name = raw.getName();
            }
            if(factionRaidConfig == null){
                factionRaidConfig = raw.getRaidConfig();
            }
            if(activationAdvancement == null){
                activationAdvancement = raw.getActivationAdvancement();
            }
            if(boostConfig == null){
                boostConfig = raw.getBoostConfig();
            }else{
                List<ResourceLocation> mandatoryBoosts = Stream.concat(boostConfig.getMandatoryResourceLocations().stream(), raw.getBoostConfig().getMandatoryResourceLocations().stream()).collect(Collectors.toList());
                List<ResourceLocation> whitelistBoosts = Stream.concat(boostConfig.getWhitelistResourceLocations().stream(), raw.getBoostConfig().getWhitelistResourceLocations().stream()).collect(Collectors.toList());
                List<ResourceLocation> blacklistBoosts = Stream.concat(boostConfig.getBlacklistResourceLocations().stream(), raw.getBoostConfig().getBlacklistResourceLocations().stream()).collect(Collectors.toList());
                List<Pair<ResourceLocation, Boost.Rarity>> rarityOverridesLocations = Stream.concat(boostConfig.getRarityOverridesLocations().stream(), raw.getBoostConfig().getRarityOverridesLocations().stream()).collect(Collectors.toList());
                boostConfig = new FactionBoostConfig(boostConfig.getBoostDistributionType(), mandatoryBoosts, whitelistBoosts, blacklistBoosts, rarityOverridesLocations);
            }
            if(factionRelations == null){
                factionRelations = raw.getRelations();
            }else{
                List<ResourceLocation> allies = Stream.concat(factionRelations.getAllies().stream(), raw.getRelations().getAllies().stream()).collect(Collectors.toList());
                List<ResourceLocation> enemies = Stream.concat(factionRelations.getEnemies().stream(), raw.getRelations().getEnemies().stream()).collect(Collectors.toList());
                factionRelations = new FactionRelations(allies, enemies);
            }
            entities.addAll(raw.getEntityTypes());
            defaultEntities = defaultEntities.merge(raw.getDefaultEntities());
        }
        if(!entities.isEmpty()){
            LOGGER.info("Entity types within the faction file is deprecated. They should now be in separate files in the faction_entity_type/<factionname>/ folder. For faction: " + id);
        }
        return new Faction(name,false, banner, factionRaidConfig, boostConfig, factionRelations, new ArrayList<>(entities), activationAdvancement, defaultEntities);
    }


    public static Faction getFaction(ResourceLocation factionResourceLocation){
        return getFactionData().getOrDefault(factionResourceLocation, Faction.GAIA);
    }

    public static boolean factionExists(ResourceLocation factionResourceLocation){
        return getFactionData().containsKey(factionResourceLocation);
    }

    public static Collection<ResourceLocation> factionKeys(){
        return getFactionData().keySet();
    }

    public static Map<ResourceLocation, Faction> getFactionData(){
        return FACTION_DATA.getData().entrySet().stream().filter(entry -> !DISABLED_FACTIONS.get().contains(entry.getKey().toString())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Collection<Faction> getActiveFactions(ServerLevel level){
        return getFactionData().values().stream().filter(faction -> isFactionActive(level, faction)).collect(Collectors.toList());
    }

    private static boolean isFactionActive(ServerLevel level, Faction faction) {
        Advancement advancement = level.getServer().getAdvancements().getAdvancement(faction.getActivationAdvancement());
        if(advancement == null) return true;
        return level.getServer().getPlayerList().getPlayers().stream().anyMatch(serverPlayerEntity -> serverPlayerEntity.getAdvancements().getOrStartProgress(advancement).isDone());
    }

    public static Faction getRandomFaction(ServerLevel level, RandomSource random, Predicate<Faction> predicate) {
        List<Faction> possibleFactions = getActiveFactions(level).stream().filter(predicate).collect(Collectors.toList());
        return GeneralUtils.getRandomItem(possibleFactions, random);
    }


    public static Faction getRandomFactionWithEnemies(ServerLevel level, RandomSource random, Predicate<Faction> predicate) {
        List<Faction> possibleFactions = getActiveFactions(level).stream().filter(predicate).collect(Collectors.toList());
        return GeneralUtils.getRandomItem(possibleFactions.stream().filter(faction -> !faction.getRelations().getEnemies().isEmpty()).collect(Collectors.toList()), random);
    }

    public static ResourceLocation getKey(Faction faction){
        if(Faction.GAIA.equals(faction)) return Faction.GAIA.getName();
        return FACTION_DATA.getData().entrySet().stream().filter(entry -> entry.getValue().equals(faction)).map(Map.Entry::getKey).findFirst().orElse(null);
    }


    public static Collection<ResourceLocation> getEnemyFactionKeysOf(Faction faction) {
        if(faction == null) return Collections.emptyList();
        return getFactionData().entrySet().stream()
                .filter(entry -> entry.getValue().getRelations().getEnemies().contains(getKey(faction)))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
