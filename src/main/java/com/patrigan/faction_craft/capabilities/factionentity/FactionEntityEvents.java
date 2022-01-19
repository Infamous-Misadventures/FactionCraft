package com.patrigan.faction_craft.capabilities.factionentity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class FactionEntityEvents {

    @SubscribeEvent
    public static void onLivingHurtEvent(LivingAttackEvent event){
        LivingEntity livingEntity = event.getEntityLiving();
        if(!livingEntity.level.isClientSide() && event.getEntityLiving() instanceof MobEntity && event.getSource().getEntity() instanceof MobEntity) {
            IFactionEntity targetCap = FactionEntityHelper.getFactionEntityCapability((MobEntity) event.getEntityLiving());
            IFactionEntity sourceCap = FactionEntityHelper.getFactionEntityCapability((MobEntity) event.getSource().getEntity());
            if(targetCap.getFaction() == sourceCap.getFaction()){
                event.setCanceled(true);
            }
        }
    }
}
