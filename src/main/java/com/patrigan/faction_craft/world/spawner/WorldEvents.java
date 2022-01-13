package com.patrigan.faction_craft.world.spawner;


import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.mixin.ServerWorldAccessor;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.stream.Collectors;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class WorldEvents {

    @SubscribeEvent
    public static void onWorldLoaded(WorldEvent.Load event){
        if(event.getWorld() instanceof ServerWorld){
            ServerWorld serverWorld = (ServerWorld) event.getWorld();
            ServerWorldAccessor accessor = castToAccessor(serverWorld);
            List<ISpecialSpawner> customSpawners = accessor.getCustomSpawners();
            List<ISpecialSpawner> newCustomSpawners = customSpawners.stream()
                    .filter(WorldEvents::filterVanillaPatrols)
                    .collect(Collectors.toList());
            newCustomSpawners.add(new com.patrigan.faction_craft.world.spawner.PatrolSpawner());
            accessor.setCustomSpawners(newCustomSpawners);

        }
    }

    public static boolean filterVanillaPatrols(ISpecialSpawner iSpecialSpawner){
        return !(iSpecialSpawner instanceof PatrolSpawner) || !FactionCraftConfig.DISABLE_VANILLA_PATROLS.get();

    }


    public static ServerWorldAccessor castToAccessor(ServerWorld serverWorld) {
        //noinspection unchecked
        return (ServerWorldAccessor)serverWorld;
    }
}
