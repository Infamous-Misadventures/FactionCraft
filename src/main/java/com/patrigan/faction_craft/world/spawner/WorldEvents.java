package com.patrigan.faction_craft.world.spawner;


import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.mixin.ServerLevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.levelgen.PatrolSpawner;
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
        if(event.getWorld() instanceof ServerLevel){
            ServerLevel serverWorld = (ServerLevel) event.getWorld();
            ServerLevelAccessor accessor = castToAccessor(serverWorld);
            List<CustomSpawner> customSpawners = accessor.getCustomSpawners();
            List<CustomSpawner> newCustomSpawners = customSpawners.stream()
                    .filter(WorldEvents::filterVanillaPatrols)
                    .collect(Collectors.toList());
            newCustomSpawners.add(new com.patrigan.faction_craft.world.spawner.PatrolSpawner());
            newCustomSpawners.add(new BattleSpawner());
            accessor.setCustomSpawners(newCustomSpawners);

        }
    }

    public static boolean filterVanillaPatrols(CustomSpawner iSpecialSpawner){
        return !(iSpecialSpawner instanceof PatrolSpawner) || !FactionCraftConfig.DISABLE_VANILLA_PATROLS.get();

    }


    public static ServerLevelAccessor castToAccessor(ServerLevel serverWorld) {
        //noinspection unchecked
        return (ServerLevelAccessor)serverWorld;
    }
}
