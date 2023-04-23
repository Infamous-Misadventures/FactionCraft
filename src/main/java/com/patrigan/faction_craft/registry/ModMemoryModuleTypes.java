package com.patrigan.faction_craft.registry;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class ModMemoryModuleTypes {

    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, MODID);

    public static final RegistryObject<MemoryModuleType<GlobalPos>> RAID_WALK_TARGET = MEMORY_MODULE_TYPES.register("raid_walk_target",
	            () -> new MemoryModuleType<>(Optional.empty()));

}
