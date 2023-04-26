package com.patrigan.faction_craft.entity.ai.brain;

import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class ModActivities {
    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(ForgeRegistries.ACTIVITIES, MODID);

    public static final RegistryObject<Activity> FACTION_RAID = register("faction_raid");
    public static final RegistryObject<Activity> PRE_FACTION_RAID = register("faction_pre_raid");
    public static final RegistryObject<Activity> FACTION_RAIDER_PREP = register("faction_raider_prep");
    public static final RegistryObject<Activity> FACTION_RAIDER_VILLAGE = register("faction_raider_village");
    public static final RegistryObject<Activity> FACTION_PATROL = register("faction_patrol");


    private static RegistryObject<Activity> register(String pKey) {
        return ACTIVITIES.register(pKey, () -> new Activity(pKey));
    }
}
