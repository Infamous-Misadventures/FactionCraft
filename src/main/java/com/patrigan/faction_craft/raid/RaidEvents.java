package com.patrigan.faction_craft.raid;

import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;
import static com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper.getRaidManagerCapability;

@Mod.EventBusSubscriber(modid = MODID)
public class RaidEvents {

    @SubscribeEvent
    public static void onWorldTickEvent(TickEvent.WorldTickEvent event){
        if(event.phase == TickEvent.Phase.END && event.side.isServer()) {
            RaidManager raidManagerCapability = getRaidManagerCapability(event.world);
            raidManagerCapability.tick();
        }
    }
}
