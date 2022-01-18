package com.patrigan.faction_craft.entity;

import com.patrigan.faction_craft.capabilities.raider.IRaider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import net.minecraft.entity.MobEntity;
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
}
