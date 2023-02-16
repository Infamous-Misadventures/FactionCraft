package com.patrigan.faction_craft.faction;

import com.patrigan.faction_craft.FactionCraft;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import com.patrigan.faction_craft.registry.FactionEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

import static com.patrigan.faction_craft.registry.FactionEntityTypes.FACTION_ENTITY_TYPE_DATA;
import static com.patrigan.faction_craft.registry.Factions.FACTION_DATA;

@Mod.EventBusSubscriber(modid = FactionCraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FactionReloadListener implements ResourceManagerReloadListener {

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event)
    {
        event.addListener(FACTION_DATA);
        event.addListener(FACTION_ENTITY_TYPE_DATA);
        event.addListener(new FactionReloadListener());
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        FACTION_DATA.getData().forEach((key, faction) -> {
            Map<ResourceLocation, FactionEntityType> factionEntityTypeData = FactionEntityTypes.getFactionEntityTypeData(faction);
            faction.addEntityTypes(factionEntityTypeData.values());
        });
    }
}
