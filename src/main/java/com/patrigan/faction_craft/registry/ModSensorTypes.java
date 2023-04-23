package com.patrigan.faction_craft.registry;

import com.patrigan.faction_craft.entity.ai.brain.sensor.FactionSpecificSensor;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class ModSensorTypes {

    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, MODID);

    public static final RegistryObject<SensorType<FactionSpecificSensor>> FACTION_SENSOR = SENSOR_TYPES.register("faction_sensor",
	            () -> new SensorType<>(FactionSpecificSensor::new));

}
