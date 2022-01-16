package com.patrigan.faction_craft.faction;

import com.patrigan.faction_craft.FactionCraft;
import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.data.util.MergeableCodecDataManager;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import com.patrigan.faction_craft.util.GeneralUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.patrigan.faction_craft.config.FactionCraftConfig.DISABLED_FACTIONS;

@Mod.EventBusSubscriber(modid = FactionCraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Factions {

    private static final MergeableCodecDataManager<Faction, Faction> FACTION_DATA = new MergeableCodecDataManager<>("faction", FactionCraft.LOGGER, Faction.CODEC, Factions::factionMerger);

    public static Faction factionMerger(List<Faction> raws){
        ResourceLocation name = null;
        CompoundNBT banner = null;
        FactionRaidConfig factionRaidConfig = null;
        FactionBoostConfig boostConfig = null;
        Set<FactionEntityType> entities = new HashSet<>();
        for (Faction raw : raws) {
            if (raw.isReplace()) {
                banner = raw.getBanner();
                name = raw.getName();
                factionRaidConfig = raw.getRaidConfig();
                boostConfig = null;
                entities = new HashSet<>();
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
            if(boostConfig == null){
                boostConfig = raw.getBoostConfig();
            }else{
                List<ResourceLocation> mandatoryBoosts = Stream.concat(boostConfig.getMandatoryResourceLocations().stream(), raw.getBoostConfig().getMandatoryResourceLocations().stream()).collect(Collectors.toList());
                List<ResourceLocation> whitelistBoosts = Stream.concat(boostConfig.getWhitelistResourceLocations().stream(), raw.getBoostConfig().getWhitelistResourceLocations().stream()).collect(Collectors.toList());
                List<ResourceLocation> blacklistBoosts = Stream.concat(boostConfig.getBlacklistResourceLocations().stream(), raw.getBoostConfig().getBlacklistResourceLocations().stream()).collect(Collectors.toList());
                boostConfig = new FactionBoostConfig(boostConfig.getBoostDistributionType(), mandatoryBoosts, whitelistBoosts, blacklistBoosts);
            }
            entities.addAll(raw.getEntityTypes());
        }
        return new Faction(name,false, banner, factionRaidConfig, boostConfig, new ArrayList<>(entities));
    }


    public static Faction getFaction(ResourceLocation factionResourceLocation){
        return getFactionData().getOrDefault(factionResourceLocation, Faction.DEFAULT);
    }

    public static boolean factionExists(ResourceLocation factionResourceLocation){
        return getFactionData().containsKey(factionResourceLocation);
    }

    public static Collection<ResourceLocation> factionKeys(){
        return getFactionData().keySet();
    }

    private static Map<ResourceLocation, Faction> getFactionData(){
        return FACTION_DATA.data.entrySet().stream().filter(entry -> !DISABLED_FACTIONS.get().contains(entry.getKey().toString())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Faction getDefaultFaction(){
        return FACTION_DATA.data.get(new ResourceLocation("vanilla"));
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event)
    {
        event.addListener(FACTION_DATA);
    }

    public static Faction getRandomFaction(Random random) {
        return GeneralUtils.getRandomItem(new ArrayList<>(FACTION_DATA.data.values()), random);
    }
}
