package com.patrigan.faction_craft.registry;

import com.patrigan.faction_craft.data.util.CodecJsonDataManager;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.patrigan.faction_craft.config.FactionCraftConfig.DISABLED_FACTIONS;
import static net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES;

public class FactionEntityTypes {

    public static final CodecJsonDataManager<FactionEntityType> FACTION_ENTITY_TYPE_DATA = new CodecJsonDataManager<>("faction_entity_type", FactionEntityType.CODEC);

    public static FactionEntityType getFactionEntityType(ResourceLocation factionResourceLocation){
        return getFactionEntityTypeData().getOrDefault(factionResourceLocation, FactionEntityType.DEFAULT);
    }

    public static boolean factionEntityTypeExists(ResourceLocation factionResourceLocation){
        return getFactionEntityTypeData().containsKey(factionResourceLocation);
    }

    public static Collection<ResourceLocation> factionEntityTypeKeys(){
        return getFactionEntityTypeData().keySet();
    }

    private static Map<ResourceLocation, FactionEntityType> getFactionEntityTypeData(){
        return FACTION_ENTITY_TYPE_DATA.getData().entrySet().stream().filter(entry -> !DISABLED_FACTIONS.get().contains(entry.getKey().toString())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<ResourceLocation, FactionEntityType> getFactionEntityTypeData(Faction faction){
        ResourceLocation key = Factions.getKey(faction);
        if(key == null) return new HashMap<>();
        return FACTION_ENTITY_TYPE_DATA.getData().entrySet().stream()
                .filter(entry -> isOfFaction(entry.getKey(), key))
                .filter(entry -> entityTypeExists(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static boolean entityTypeExists(FactionEntityType value) {
        return ENTITY_TYPES.containsKey(value.getEntityType());
    }

    private static boolean isOfFaction(ResourceLocation entityType, ResourceLocation faction) {
        return entityType.getNamespace().equals(faction.getNamespace()) && entityType.getPath().startsWith(faction.getPath());
    }

}
