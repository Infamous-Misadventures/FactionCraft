package com.patrigan.faction_craft.entity.ai.brain;

import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class ModActivities {
    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(ForgeRegistries.ACTIVITIES, MODID);

    public static final RegistryObject<Activity> FACTION_RAID = register("faction_raid");
    public static final RegistryObject<Activity> PRE_FACTION_RAID = register("faction_pre_raid");


    private static RegistryObject<Activity> register(String pKey) {
        return ACTIVITIES.register(pKey, () -> new Activity(pKey));
    }
}
