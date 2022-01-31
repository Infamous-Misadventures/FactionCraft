package com.patrigan.faction_craft.faction;

import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class FactionEvents {

    @SubscribeEvent
    public static void onLivingHurtEvent(LivingAttackEvent event){
        LivingEntity livingEntity = event.getEntityLiving();
        if(!livingEntity.level.isClientSide() && event.getEntityLiving() instanceof Mob && event.getSource().getEntity() instanceof Mob) {
            FactionEntity targetCap = FactionEntityHelper.getFactionEntityCapability((Mob) event.getEntityLiving());
            FactionEntity sourceCap = FactionEntityHelper.getFactionEntityCapability((Mob) event.getSource().getEntity());
            if(targetCap != null && sourceCap != null && targetCap.getFaction() != null && sourceCap.getFaction() != null) {
                if (targetCap.getFaction() == sourceCap.getFaction() || sourceCap.getFaction().getRelations().getAllies().contains(targetCap.getFaction().getName())) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
