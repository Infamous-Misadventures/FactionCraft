package com.patrigan.faction_craft.faction;

import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.boost.Boosts;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.factionentity.IFactionEntity;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.server.ServerWorld;

import java.util.*;
import java.util.stream.Collectors;

import static com.patrigan.faction_craft.util.GeneralUtils.getRandomItem;

public class FactionBoostHelper {

    public static int applyBoosts(int targetStrength, List<MobEntity> flatEntities, Faction faction, ServerWorld level) {
        if(flatEntities.isEmpty()){
            return 0;
        }
        switch(faction.getBoostConfig().getBoostDistributionType()){
            case UNIFORM_ALL:
                return applyUniformAll(targetStrength, flatEntities, level, faction);
            case UNIFORM_TYPE:
                return applyUniformType(targetStrength, flatEntities, level, faction);
            case LEADER:
            case STRONG_FAVOURED:
            case WEAK_FAVOURED:
            case RANDOM:
            default:
                return applyRandom(targetStrength, flatEntities, level, faction);
        }
    }

    private static int applyUniformAll(int targetStrength, List<MobEntity> entities, ServerWorld level, Faction faction) {
        int appliedStrength = 0;
        Random random = level.random;
        while(appliedStrength+entities.size() <= targetStrength) {
            Boost boost = Boosts.getRandomBoost(random, faction.getBoostConfig().getWhitelistBoosts(), faction.getBoostConfig().getBlacklistBoosts());
            if(boost == null){
                break;
            }
            Optional<Integer> reduce = entities.stream().map(boost::apply).reduce(Integer::sum);
            if(reduce.isPresent()){
                appliedStrength += reduce.get();
            }
        }
        return appliedStrength;
    }

    private static int applyUniformType(int targetStrength, List<MobEntity> entities, ServerWorld level, Faction faction) {
        int appliedStrength = 0;
        Random random = level.random;
        Set<FactionEntityType> factionEntityTypes = entities.stream().map(mobEntity -> FactionEntityHelper.getFactionEntityCapability(mobEntity).getFactionEntityType()).collect(Collectors.toSet());
        while(appliedStrength < targetStrength) {
            FactionEntityType randomFactionEntityType = getRandomItem(new ArrayList<>(factionEntityTypes), random);
            List<MobEntity> entitiesWithType = entities.stream().filter(mobEntity -> randomFactionEntityType.equals(FactionEntityHelper.getFactionEntityCapability(mobEntity).getFactionEntityType())).collect(Collectors.toList());
            if(appliedStrength+entitiesWithType.size() > targetStrength){
                break;
            }
            MobEntity randomEntity = entitiesWithType.get(0);
            Boost boost = Boosts.getRandomBoostForEntity(random, randomEntity, getWhitelistBoosts(faction, randomFactionEntityType), getBlacklistBoosts(faction, randomFactionEntityType), getRarityOverrides(faction, randomFactionEntityType));
            if(boost == null){
                break;
            }
            Optional<Integer> reduce = entitiesWithType.stream().map(boost::apply).reduce(Integer::sum);
            if(reduce.isPresent()){
                appliedStrength += reduce.get();
            }
        }
        return appliedStrength;
    }

    private static int applyRandom(int targetStrength, List<MobEntity> entities, ServerWorld level, Faction faction) {
        int appliedStrength = 0;
        Random random = level.random;
        while(appliedStrength < targetStrength) {
            MobEntity randomEntity = getRandomItem(entities, random);
            IFactionEntity cap = FactionEntityHelper.getFactionEntityCapability(randomEntity);
            FactionEntityType factionEntityType = cap.getFactionEntityType();
            Boost boost = Boosts.getRandomBoostForEntity(random, randomEntity, getWhitelistBoosts(faction, factionEntityType), getBlacklistBoosts(faction, factionEntityType), getRarityOverrides(faction, factionEntityType));
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
