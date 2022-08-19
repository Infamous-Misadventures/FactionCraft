package com.patrigan.faction_craft.level.spawner;


import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.mixin.ServerLevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.stream.Collectors;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class LevelEvents {

    @SubscribeEvent
    public static void onLevelLoaded(LevelEvent.Load event){
        if(event.getLevel() instanceof ServerLevel){
            ServerLevel serverLevel = (ServerLevel) event.getLevel();
            ServerLevelAccessor accessor = castToAccessor(serverLevel);
            List<CustomSpawner> customSpawners = accessor.getCustomSpawners();
            List<CustomSpawner> newCustomSpawners = customSpawners.stream()
                    .filter(LevelEvents::filterVanillaPatrols)
                    .collect(Collectors.toList());
            newCustomSpawners.add(new com.patrigan.faction_craft.level.spawner.PatrolSpawner());
            newCustomSpawners.add(new com.patrigan.faction_craft.level.spawner.BattleSpawner());
            accessor.setCustomSpawners(newCustomSpawners);

        }
    }

    public static boolean filterVanillaPatrols(CustomSpawner iSpecialSpawner){
        return !(iSpecialSpawner instanceof PatrolSpawner) || !FactionCraftConfig.DISABLE_VANILLA_PATROLS.get();

    }


    public static ServerLevelAccessor castToAccessor(ServerLevel serverLevel) {
        //noinspection unchecked
        return (ServerLevelAccessor)serverLevel;
    }
}
