package com.patrigan.faction_craft.boost;


import com.patrigan.faction_craft.capabilities.appliedboosts.AppliedBoostsHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class BoostEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event){
        Entity entity = event.getEntity();
        if(!entity.level.isClientSide() && entity instanceof Mob) {
            Mob mobEntity = (Mob) event.getEntity();
            AppliedBoostsHelper.getAppliedBoostsCapability(mobEntity).getAppliedBoosts().forEach(boost -> boost.applyAIChanges(mobEntity));
        }
    }
}
