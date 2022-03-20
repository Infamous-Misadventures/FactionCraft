package com.patrigan.faction_craft.faction;

import com.mojang.datafixers.util.Pair;
import com.patrigan.faction_craft.FactionCraft;
import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.data.util.MergeableCodecDataManager;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import com.patrigan.faction_craft.util.GeneralUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
        FactionRelations factionRelations = null;
        Set<FactionEntityType> entities = new HashSet<>();
        ResourceLocation activationAdvancement = null;
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
        }
        return new Faction(name,false, banner, factionRaidConfig, boostConfig, factionRelations, new ArrayList<>(entities), activationAdvancement);
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

    private static Collection<Faction> getActiveFactions(ServerWorld level){
        return getFactionData().values().stream().filter(faction -> isFactionActive(level, faction)).collect(Collectors.toList());
    }

    private static boolean isFactionActive(ServerWorld level, Faction faction) {
        Advancement advancement = level.getServer().getAdvancements().getAdvancement(faction.getActivationAdvancement());
        if(advancement == null) return true;
        return level.getServer().getPlayerList().getPlayers().stream().anyMatch(serverPlayerEntity -> serverPlayerEntity.getAdvancements().getOrStartProgress(advancement).isDone());
    }

    public static Faction getDefaultFaction(){
        return FACTION_DATA.data.get(new ResourceLocation("illager"));
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event)
    {
        event.addListener(FACTION_DATA);
    }

    public static Faction getRandomFaction(ServerWorld level, Random random) {
        return GeneralUtils.getRandomItem(new ArrayList<>(getActiveFactions(level)), random);
    }
    public static Faction getRandomFactionWithEnemies(ServerWorld level, Random random) {
        return GeneralUtils.getRandomItem(getActiveFactions(level).stream().filter(faction -> !faction.getRelations().getEnemies().isEmpty()).collect(Collectors.toList()), random);
    }
}
