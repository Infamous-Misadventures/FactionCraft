package com.patrigan.faction_craft.entity;

import com.patrigan.faction_craft.capabilities.patroller.Patroller;
import com.patrigan.faction_craft.capabilities.patroller.PatrollerHelper;
import com.patrigan.faction_craft.capabilities.raider.Raider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.entity.ai.target.FactionAllyHurtTargetGoal;
import com.patrigan.faction_craft.entity.ai.target.NearestFactionEnemyTargetGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class EntityEvents {

    @SubscribeEvent
    public static void onLivingConversionEvent(LivingConversionEvent.Pre event){
        if(event.getEntity() instanceof Mob mob){
            Raider raiderCap = RaiderHelper.getRaiderCapability(mob);
            Patroller patrollerCap = PatrollerHelper.getPatrollerCapability(mob);
            if(raiderCap == null || patrollerCap == null) return;
            if(raiderCap.hasActiveRaid() || patrollerCap.isPatrolling()){
                event.setCanceled(true);
                event.setConversionTimer(0);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof Mob){
            Mob mob = (Mob) entity;
            mob.targetSelector.addGoal(2, new NearestFactionEnemyTargetGoal(mob, 10, true, false));
            mob.targetSelector.addGoal(2, new FactionAllyHurtTargetGoal(mob, 10, true, false));
        }
    }
}
