package com.patrigan.faction_craft.entity;

import com.patrigan.faction_craft.capabilities.raider.Raider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.entity.ai.goal.NearestFactionEnemyTargetGoal;
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
        if(event.getEntity() instanceof Mob){
            Raider cap = RaiderHelper.getRaiderCapability((Mob) event.getEntity());
            if(cap.hasActiveRaid()){
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
            mob.targetSelector.addGoal(2, new NearestFactionEnemyTargetGoal<>(mob, 10, true, false));
        }
    }
}
