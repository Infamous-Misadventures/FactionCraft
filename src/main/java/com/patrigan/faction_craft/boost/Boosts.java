package com.patrigan.faction_craft.boost;

import com.patrigan.faction_craft.FactionCraft;
import com.patrigan.faction_craft.capabilities.appliedboosts.AppliedBoostsHelper;
import com.patrigan.faction_craft.capabilities.appliedboosts.IAppliedBoosts;
import com.patrigan.faction_craft.data.util.CodecJsonDataManager;
import com.patrigan.faction_craft.util.GeneralUtils;
import net.minecraft.entity.LivingEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = FactionCraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Boosts {

    public static final CodecJsonDataManager<Boost> BOOSTS = new CodecJsonDataManager<>("boost", Boost.CODEC, FactionCraft.LOGGER);


    public static Boost getBoost(ResourceLocation factionResourceLocation){
        return BOOSTS.data.getOrDefault(factionResourceLocation, NoBoost.INSTANCE);
    }

    public static boolean boostExists(ResourceLocation boostResourceLocation){
        return BOOSTS.data.containsKey(boostResourceLocation);
    }

    public static Collection<ResourceLocation> boostKeys(){
        return BOOSTS.data.keySet();
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event)
    {
        event.addListener(BOOSTS);
    }

    public static Boost getRandomBoost(Random random) {
        if(BOOSTS.data.size() == 0){
            return null;
        }
        return GeneralUtils.getRandomItem(new ArrayList<>(BOOSTS.data.values()), random);
    }

    public static Boost getRandomBoost(Random random, List<Boost> whitelist, List<Boost> blacklist) {
        if(BOOSTS.data.size() == 0){
            return null;
        }
        List<Boost> filtered = BOOSTS.data.values().stream().filter(boost -> (whitelist.isEmpty() && boost.getType() != Boost.BoostType.SPECIAL) || whitelist.contains(boost)).filter(boost -> !blacklist.contains(boost)).collect(Collectors.toList());
        return GeneralUtils.getRandomItem(filtered, random);
    }

    public static Boost getRandomBoostForEntity(Random random, LivingEntity livingEntity, List<Boost> whitelist, List<Boost> blacklist, Map<Boost, Boost.Rarity> rarityOverrides) {
        if(BOOSTS.data.size() == 0){
            return null;
        }
        LazyOptional<IAppliedBoosts> lazyCap = AppliedBoostsHelper.getAppliedBoostsCapabilityLazy(livingEntity);
        if(!lazyCap.isPresent()){
            return null;
        }
        IAppliedBoosts cap = lazyCap.resolve().get();
        List<Pair<Boost, Integer>> filtered = BOOSTS.data.values().stream()
                .filter(boost -> (whitelist.isEmpty() && !getRarity(boost, rarityOverrides).equals(Boost.Rarity.NONE)) || whitelist.contains(boost))
                .filter(boost -> !blacklist.contains(boost))
                .filter(boost -> cap.getBoostsOfType(boost.getType()).size() < boost.getType().getMax())
                .filter(boost -> boost.canApply(livingEntity))
                .map(boost -> new Pair<>(boost, getRarity(boost, rarityOverrides).getWeight()))
                .collect(Collectors.toList());
        if(filtered.size() == 0){
            return null;
        }
        return GeneralUtils.getRandomEntry(filtered, random);
    }

    private static Boost.Rarity getRarity(Boost boost, Map<Boost, Boost.Rarity> rarityOverrides){
        if(rarityOverrides.containsKey(boost)){
            return rarityOverrides.get(boost);
        }else{
            return boost.getRarity();
        }
    }
}
