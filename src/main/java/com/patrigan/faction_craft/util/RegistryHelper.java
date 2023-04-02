package com.patrigan.faction_craft.util;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.RegistryManager;

import java.util.Optional;

public class RegistryHelper {

    private static <T> T loadForgeObject(ResourceLocation resourceLocation, ResourceKey<? extends Registry<T>> registryKey) {
        return RegistryManager.ACTIVE.getRegistry(registryKey).getValue(resourceLocation);
    }

    public static <T> T loadVanillaObject(Registry<T> registry, ResourceLocation resourceLocation) {
        return registry.get(resourceLocation);
    }

    public static <T> T loadFromRegistry(ResourceLocation resourceLocation, ResourceKey<? extends Registry<T>> registryKey, RegistryAccess registryAccess) {
        Optional<? extends Registry<T>> vanillaRegistry = registryAccess.registry(registryKey);
        return vanillaRegistry.map(registry -> loadVanillaObject(registry, resourceLocation)).orElseGet(() -> loadForgeObject(resourceLocation, registryKey));
    }

}
