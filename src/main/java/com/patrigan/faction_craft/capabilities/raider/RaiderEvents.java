package com.patrigan.faction_craft.capabilities.raider;

import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class RaiderEvents {

    @SubscribeEvent
    public static void onLivingHurtEvent(LivingHurtEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!livingEntity.level.isClientSide() && livingEntity instanceof Mob mob) {
            Raider cap = RaiderHelper.getRaiderCapability(mob);
            if (cap.hasActiveRaid()) {
                cap.getRaid().updateBossbar();
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!livingEntity.level.isClientSide() && livingEntity instanceof Mob mob) {
            Raider cap = RaiderHelper.getRaiderCapability(mob);
            if (cap.hasActiveRaid()) {
                Raid raid = cap.getRaid();
                raid.updateBossbar();
                if (cap.isWaveLeader()) {
                    raid.removeLeader(cap.getWave());
                }
                if (event.getSource().getEntity() != null && event.getSource().getEntity().getType() == EntityType.PLAYER) {
                    raid.addHeroOfTheVillage(event.getSource().getEntity());
                }
                raid.removeFromRaid(mob, cap.getWave(), false);
            }
        }
    }

//    @SubscribeEvent
//    public static void onAllowDespawn(LivingSpawnEvent.AllowDespawn event){
//        LivingEntity livingEntity = event.getEntityLiving();
//        if(!livingEntity.level.isClientSide() && livingEntity instanceof MobEntity) {
//            MobEntity mobEntity = (MobEntity) livingEntity;
//            RaiderHelper.getRaiderCapabilityLazy(mobEntity).ifPresent(cap -> {
//                if (cap.hasActiveRaid()) {
//                    cap.getRaid().removeFromRaid(mobEntity, cap.getWave(), true);
//                }
//            });
//        }
//    }

    @SubscribeEvent
    public static void onAllowDespawn(LivingSpawnEvent.AllowDespawn event) {
        Mob mob = event.getEntity();
        if (!mob.level.isClientSide()) {
            Raider cap = RaiderHelper.getRaiderCapability(mob);
            if (cap.hasActiveRaid()) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
