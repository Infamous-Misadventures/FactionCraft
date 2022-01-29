package com.patrigan.faction_craft.capabilities.patroller;

import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factioninteraction.FactionInteractionHelper;
import com.patrigan.faction_craft.capabilities.factioninteraction.FactionInteraction;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.effect.Effects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.util.Mth;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class PatrollerEvents {

    @SubscribeEvent
    public static void onLivingHurtEvent(LivingHurtEvent event){
        LivingEntity livingEntity = event.getEntityLiving();
        if(!livingEntity.level.isClientSide() && livingEntity instanceof Mob) {
            PatrollerHelper.getPatrollerCapabilityLazy((Mob) livingEntity).ifPresent(cap -> {
            });
        }
    }
    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event){
        LivingEntity livingEntity = event.getEntityLiving();
        Entity sourceEntity = event.getSource().getEntity();
        if(!livingEntity.level.isClientSide() && livingEntity instanceof Mob && sourceEntity instanceof Player) {
            Player playerEntity = (Player) sourceEntity;
            PatrollerHelper.getPatrollerCapabilityLazy((Mob) livingEntity).ifPresent(cap -> {
                if(cap.isPatrolLeader()){
                    MobEffectInstance effectinstance1 = playerEntity.getEffect(Effects.FACTION_BAD_OMEN);
                    int i = 1;
                    if (effectinstance1 != null) {
                        i += effectinstance1.getAmplifier();
                        playerEntity.removeEffectNoUpdate(Effects.FACTION_BAD_OMEN);
                    } else {
                        --i;
                    }

                    i = Mth.clamp(i, 0, FactionCraftConfig.RAID_MAX_FACTIONS.get()-1);
                    MobEffectInstance effectinstance = new MobEffectInstance(Effects.FACTION_BAD_OMEN, 120000, i, false, false, true);
                    if (!FactionCraftConfig.DISABLE_FACTION_RAIDS.get()) {
                        FactionInteraction factionInteractionCapability = FactionInteractionHelper.getFactionInteractionCapability(playerEntity);
                        if(factionInteractionCapability.getBadOmenFactions().size() < FactionCraftConfig.RAID_MAX_FACTIONS.get()) {
                            FactionEntity factionEntityCapability = FactionEntityHelper.getFactionEntityCapability((Mob) livingEntity);
                            if (factionEntityCapability.getFaction() != null){
                                factionInteractionCapability.addBadOmenFaction(factionEntityCapability.getFaction());
                            }
                        }
                        playerEntity.addEffect(effectinstance);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onAllowDespawn(LivingSpawnEvent.AllowDespawn event){
        LivingEntity livingEntity = event.getEntityLiving();
        if(!livingEntity.level.isClientSide() && livingEntity instanceof Mob) {
            Mob mobEntity = (Mob) livingEntity;
            PatrollerHelper.getPatrollerCapabilityLazy(mobEntity).ifPresent(cap -> {
                if (cap.isPatrolling()) {
                    event.setResult(Event.Result.DENY);
                }
            });
        }
    }

}
