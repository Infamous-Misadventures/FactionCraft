package com.patrigan.faction_craft.capabilities;

import com.patrigan.faction_craft.capabilities.appliedboosts.AppliedBoosts;
import com.patrigan.faction_craft.capabilities.appliedboosts.AttacherAppliedBoosts;
import com.patrigan.faction_craft.capabilities.dominion.AttacherDominion;
import com.patrigan.faction_craft.capabilities.dominion.Dominion;
import com.patrigan.faction_craft.capabilities.factionentity.AttacherFactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factioninteraction.AttacherFactionInteraction;
import com.patrigan.faction_craft.capabilities.factioninteraction.FactionInteraction;
import com.patrigan.faction_craft.capabilities.playerfactions.AttacherPlayerFactions;
import com.patrigan.faction_craft.capabilities.playerfactions.PlayerFactions;
import com.patrigan.faction_craft.capabilities.savedfactiondata.AttacherSavedFactionData;
import com.patrigan.faction_craft.capabilities.patroller.AttacherPatroller;
import com.patrigan.faction_craft.capabilities.patroller.Patroller;
import com.patrigan.faction_craft.capabilities.raider.AttacherRaider;
import com.patrigan.faction_craft.capabilities.raider.Raider;
import com.patrigan.faction_craft.capabilities.raidmanager.AttacherRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.savedfactiondata.SavedFactionData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCapabilities {

    public static final Capability<AppliedBoosts> APPLIED_BOOSTS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<FactionEntity> FACTION_ENTITY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<FactionInteraction> FACTION_INTERACTION_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<Patroller> PATROLLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<Raider> RAIDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<RaidManager> RAID_MANAGER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<SavedFactionData> SAVED_FACTION_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<Dominion> DOMINION_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<PlayerFactions> PLAYER_FACTIONS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});


    public static void setupCapabilities() {
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addGenericListener(Entity.class, AttacherAppliedBoosts::attach);
        forgeBus.addGenericListener(Entity.class, AttacherFactionEntity::attach);
        forgeBus.addGenericListener(Entity.class, AttacherFactionInteraction::attach);
        forgeBus.addGenericListener(Entity.class, AttacherPatroller::attach);
        forgeBus.addGenericListener(Entity.class, AttacherRaider::attach);
        forgeBus.addGenericListener(Level.class, AttacherRaidManager::attach);
        forgeBus.addGenericListener(Level.class, AttacherSavedFactionData::attach);
        forgeBus.addGenericListener(Level.class, AttacherPlayerFactions::attach);
        forgeBus.addGenericListener(Level.class, AttacherDominion::attach);
    }

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(AppliedBoosts.class);
        event.register(FactionEntity.class);
        event.register(FactionInteraction.class);
        event.register(Patroller.class);
        event.register(Raider.class);
        event.register(RaidManager.class);
        event.register(SavedFactionData.class);
        event.register(PlayerFactions.class);
        event.register(Dominion.class);
    }
}
