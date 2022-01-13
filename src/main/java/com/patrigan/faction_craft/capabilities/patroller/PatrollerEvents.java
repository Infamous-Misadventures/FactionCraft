package com.patrigan.faction_craft.capabilities.patroller;

import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
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
        if(!livingEntity.level.isClientSide() && livingEntity instanceof MobEntity) {
            PatrollerHelper.getPatrollerCapabilityLazy((MobEntity) livingEntity).ifPresent(cap -> {
            });
        }
    }

}
