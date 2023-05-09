package com.patrigan.faction_craft.faction;

import com.patrigan.faction_craft.FactionCraft;
import com.patrigan.faction_craft.capabilities.playerfactions.PlayerFactions;
import com.patrigan.faction_craft.capabilities.playerfactions.PlayerFactionsHelper;
import com.patrigan.faction_craft.capabilities.savedfactiondata.SavedFactionData;
import com.patrigan.faction_craft.capabilities.savedfactiondata.SavedFactionDataHelper;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import com.patrigan.faction_craft.registry.FactionEntityTypes;
import com.patrigan.faction_craft.registry.Factions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Map;

import static com.patrigan.faction_craft.faction.relations.FactionRelation.ALLY_MAX;
import static com.patrigan.faction_craft.faction.relations.FactionRelation.ENEMY_MAX;
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
            addEntities(faction);
            updateInitialRelationships(faction);
            updateActualRelationships(faction);
        });
        addPlayerFactions();
    }

    private void addPlayerFactions() {
        PlayerFactions playerFactions = PlayerFactionsHelper.getPlayerFactions();
        playerFactions.getPlayerFactions().forEach((uuid, playerFaction) -> Factions.addPlayerFaction(playerFaction.getFaction()));
    }

    private void updateActualRelationships(Faction faction) {
        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        if(currentServer == null) return;
        SavedFactionData savedFactionData = SavedFactionDataHelper.getCapability(currentServer.overworld());
        faction.getRelations().initiateActualRelations(savedFactionData.getOriginalRelations(faction));
    }

    private void updateInitialRelationships(Faction faction) {
        faction.getRelations().getEnemies().forEach(enemy -> {
            FACTION_DATA.getData().get(enemy).getRelations().setInitialRelation(faction, ENEMY_MAX);
        });
        faction.getRelations().getAllies().forEach(ally -> {
            FACTION_DATA.getData().get(ally).getRelations().setInitialRelation(faction, ALLY_MAX);
        });
    }

    private void addEntities(Faction faction) {
        Map<ResourceLocation, FactionEntityType> factionEntityTypeData = FactionEntityTypes.getFactionEntityTypeData(faction);
        faction.addEntityTypes(factionEntityTypeData.values());
    }
}
