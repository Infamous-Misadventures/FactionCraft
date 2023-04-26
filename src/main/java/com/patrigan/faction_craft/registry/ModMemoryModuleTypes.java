package com.patrigan.faction_craft.registry;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Optional;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class ModMemoryModuleTypes {

    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, MODID);

    public static final RegistryObject<MemoryModuleType<GlobalPos>> RAID_WALK_TARGET = MEMORY_MODULE_TYPES.register("raid_walk_target",
	            () -> new MemoryModuleType<>(Optional.empty()));

    public static final RegistryObject<MemoryModuleType<LivingEntity>> NEAREST_VISIBLE_FACTION_ENEMY = MEMORY_MODULE_TYPES.register("nearest_visible_faction_enemy",
            () -> new MemoryModuleType<>(Optional.empty()));

    public static final RegistryObject<MemoryModuleType<LivingEntity>> NEAREST_VISIBLE_FACTION_ALLY = MEMORY_MODULE_TYPES.register("nearest_visible_faction_ally",
            () -> new MemoryModuleType<>(Optional.empty()));

    public static final RegistryObject<MemoryModuleType<LivingEntity>> NEAREST_VISIBLE_DAMAGED_FACTION_ALLY = MEMORY_MODULE_TYPES.register("nearest_visible_damaged_faction_ally",
            () -> new MemoryModuleType<>(Optional.empty()));

    public static final RegistryObject<MemoryModuleType<List<GlobalPos>>> RAIDED_VILLAGE_POI = MEMORY_MODULE_TYPES.register("raided_village_poi",
            () -> new MemoryModuleType<>(Optional.empty()));

    public static final RegistryObject<MemoryModuleType<Raid>> RAID = MEMORY_MODULE_TYPES.register("faction_raid",
            () -> new MemoryModuleType<>(Optional.empty()));

    public static final RegistryObject<MemoryModuleType<Boolean>> PATROLLER = MEMORY_MODULE_TYPES.register("patroller",
            () -> new MemoryModuleType<>(Optional.empty()));
}
