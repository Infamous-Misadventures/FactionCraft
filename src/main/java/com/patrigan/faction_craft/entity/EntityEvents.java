package com.patrigan.faction_craft.entity;

import com.patrigan.faction_craft.capabilities.raider.IRaider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.entity.ai.goal.NearestFactionEnemyTargetGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class EntityEvents {

    @SubscribeEvent
    public static void onLivingConversionEvent(LivingConversionEvent.Pre event){
        if(event.getEntity() instanceof MobEntity){
            IRaider cap = RaiderHelper.getRaiderCapability((MobEntity) event.getEntity());
            if(cap.hasActiveRaid()){
                event.setCanceled(true);
                event.setConversionTimer(0);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof MobEntity){
            MobEntity mob = (MobEntity) entity;
            mob.targetSelector.addGoal(2, new NearestFactionEnemyTargetGoal<>(mob, 10, true, false));
        }
    }
}
