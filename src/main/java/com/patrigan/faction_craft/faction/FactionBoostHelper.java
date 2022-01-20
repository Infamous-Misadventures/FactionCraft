package com.patrigan.faction_craft.faction;

import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.boost.Boosts;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.server.ServerWorld;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.patrigan.faction_craft.util.GeneralUtils.getRandomItem;

public class FactionBoostHelper {

    public static int applyBoosts(int targetStrength, Map<FactionEntityType, List<MobEntity>> entities, Faction faction, ServerWorld level) {
        if(entities.isEmpty()){
            return 0;
        }
        switch(faction.getBoostConfig().getBoostDistributionType()){
            case UNIFORM_ALL:
                return applyUniformAll(targetStrength, entities, level, faction);
            case UNIFORM_TYPE:
                return applyUniformType(targetStrength, entities, level, faction);
            case LEADER:
            case STRONG_FAVOURED:
            case WEAK_FAVOURED:
            case RANDOM:
            default:
                return applyRandom(targetStrength, entities, level, faction);
        }
    }

    private static int applyUniformAll(int targetStrength, Map<FactionEntityType, List<MobEntity>> entityTypeListMap, ServerWorld level, Faction faction) {
        int appliedStrength = 0;
        Random random = level.random;
        List<MobEntity> allEntities = entityTypeListMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        while(appliedStrength+allEntities.size() <= targetStrength) {
            Boost boost = Boosts.getRandomBoost(random, faction.getBoostConfig().getWhitelistBoosts(), faction.getBoostConfig().getBlacklistBoosts());
            if(boost == null){
                break;
            }
            Optional<Integer> reduce = allEntities.stream().map(boost::apply).reduce(Integer::sum);
            if(reduce.isPresent()){
                appliedStrength += reduce.get();
            }
        }
        return appliedStrength;
    }

    private static int applyUniformType(int targetStrength, Map<FactionEntityType, List<MobEntity>> entities, ServerWorld level, Faction faction) {
        int appliedStrength = 0;
        Random random = level.random;
        while(appliedStrength < targetStrength) {
            FactionEntityType randomFactionEntityType = getRandomItem(new ArrayList<>(entities.keySet()), random);
            if(appliedStrength+entities.get(randomFactionEntityType).size() > targetStrength){
                break;
            }
            MobEntity randomEntity = entities.get(randomFactionEntityType).get(0);
            Boost boost = Boosts.getRandomBoostForEntity(random, randomEntity, getWhitelistBoosts(faction, randomFactionEntityType), getBlacklistBoosts(faction, randomFactionEntityType), getRarityOverrides(faction, randomFactionEntityType));
            if(boost == null){
                break;
            }
            Optional<Integer> reduce = entities.get(randomFactionEntityType).stream().map(boost::apply).reduce(Integer::sum);
            if(reduce.isPresent()){
                appliedStrength += reduce.get();
            }
        }
        return appliedStrength;
    }

    private static int applyRandom(int targetStrength, Map<FactionEntityType, List<MobEntity>> entities, ServerWorld level, Faction faction) {
        int appliedStrength = 0;
        Random random = level.random;
        while(appliedStrength < targetStrength) {
            FactionEntityType randomFactionEntityType = getRandomItem(new ArrayList<>(entities.keySet()), random);
            MobEntity randomEntity = getRandomItem(entities.get(randomFactionEntityType), random);
            Boost boost = Boosts.getRandomBoostForEntity(random, randomEntity, getWhitelistBoosts(faction, randomFactionEntityType), getBlacklistBoosts(faction, randomFactionEntityType), getRarityOverrides(faction, randomFactionEntityType));
            if(boost == null){
                break;
            }
            appliedStrength += boost.apply(randomEntity);
        }
        return appliedStrength;
    }

    public static List<Boost> getWhitelistBoosts(Faction faction, FactionEntityType factionEntityType){
        List<Boost> resultList = new ArrayList<>();
        resultList.addAll(faction.getBoostConfig().getWhitelistBoosts());
        resultList.addAll(factionEntityType.getBoostConfig().getWhitelistBoosts());
        return resultList;
    }

    public static List<Boost> getBlacklistBoosts(Faction faction, FactionEntityType factionEntityType){
        List<Boost> resultList = new ArrayList<>();
        resultList.addAll(faction.getBoostConfig().getBlacklistBoosts());
        resultList.addAll(factionEntityType.getBoostConfig().getBlacklistBoosts());
        return resultList;
    }

    public static Map<Boost, Boost.Rarity> getRarityOverrides(Faction faction, FactionEntityType factionEntityType){
        Map<Boost, Boost.Rarity> resultMap = new HashMap<>();
        resultMap.putAll(faction.getBoostConfig().getRarityOverrides());
        resultMap.putAll(factionEntityType.getBoostConfig().getRarityOverrides());
        return resultMap;
    }
}
