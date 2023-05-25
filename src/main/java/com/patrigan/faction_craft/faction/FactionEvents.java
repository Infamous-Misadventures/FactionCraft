package com.patrigan.faction_craft.faction;

import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.playerfactions.PlayerFactions;
import com.patrigan.faction_craft.capabilities.playerfactions.PlayerFactionsHelper;
import com.patrigan.faction_craft.registry.Factions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;
import static com.patrigan.faction_craft.registry.Factions.reloadPlayerFactions;
import static net.minecraft.world.level.Level.OVERWORLD;

@Mod.EventBusSubscriber(modid = MODID)
public class FactionEvents {

    @SubscribeEvent
    public static void onLivingChangeTargetEvent(LivingChangeTargetEvent event){
        LivingEntity livingEntity = event.getEntity();
        if(!livingEntity.level.isClientSide()) {
            FactionEntity sourceCap = FactionEntityHelper.getFactionEntityCapability(event.getEntity());
            FactionEntity targetCap = FactionEntityHelper.getFactionEntityCapability(event.getOriginalTarget());
            if (targetCap.getFaction() == sourceCap.getFaction() || sourceCap.getFaction().isAllyOf(targetCap.getFaction())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtEvent(LivingAttackEvent event){
        LivingEntity livingEntity = event.getEntity();
        if(!livingEntity.level.isClientSide() && event.getEntity() instanceof Mob && event.getSource().getEntity() instanceof Mob) {
            FactionEntity targetCap = FactionEntityHelper.getFactionEntityCapability((Mob) event.getEntity());
            FactionEntity sourceCap = FactionEntityHelper.getFactionEntityCapability((Mob) event.getSource().getEntity());
            if (targetCap.getFaction() == sourceCap.getFaction() || sourceCap.getFaction().isAllyOf(targetCap.getFaction())) {
                event.setCanceled(true);
            }
        }
    }

    // On player join event, check if Player Factions already has the correct player faction, if not, create it and add it to both the Player Factions and Factions
    @SubscribeEvent
    public static void onPlayerJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.level.isClientSide()) return;
        PlayerFactions playerFactions = PlayerFactionsHelper.getPlayerFactions();
        if (!playerFactions.hasPlayerFaction(player)) {
            Faction faction = Factions.createPlayerFaction(player);
            playerFactions.addPlayerFaction(player, faction);
        }
        FactionEntity factionEntityCapability = FactionEntityHelper.getFactionEntityCapability(player);
        if(!factionEntityCapability.hasFaction()) {
            factionEntityCapability.setFaction(playerFactions.getPlayerFaction(player));
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        LevelAccessor levelAccessor = event.getLevel();
        if(levelAccessor.isClientSide()) return;
        if(levelAccessor instanceof Level && ((Level) levelAccessor).dimension().equals(OVERWORLD)) {
            reloadPlayerFactions();
        }
    }
}
