package com.patrigan.faction_craft.network;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;


@Mod.EventBusSubscriber(modid = MODID)
public class MessageEvents {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
//        if (player instanceof ServerPlayerEntity)
//            getEnchantableCapabilityLazy(player).ifPresent(cap -> {
//                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new MobEnchantmentMessage(player.getId(), cap.getEnchantments()));
//            });
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event){
        Player player = event.getEntity();
        Entity target = event.getTarget();
//        if (player instanceof ServerPlayerEntity)
//            getEnchantableCapabilityLazy(target).ifPresent(cap -> {
//                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new MobEnchantmentMessage(target.getId(), cap.getEnchantments()));
//            });
    }
}
