package com.patrigan.faction_craft.tags;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.RegistryManager;

import java.util.Optional;

// Thanks to Tslat for this code
public class TagHelper {

    public static <T> boolean isForgeObjectTaggedAs(T object, TagKey<T> tagKey) {
        return RegistryManager.ACTIVE.getRegistry(tagKey.registry()).tags().getTag(tagKey).contains(object);
    }

    public static <T> boolean isVanillaObjectTaggedAs(Registry<T> registry, T object, TagKey<T>  tagKey) {
        return registry.getHolderOrThrow(registry.getResourceKey(object).orElseThrow()).is(tagKey);
    }

    public static <T> boolean isTaggedAs(T object, TagKey<T> tagKey, RegistryAccess registryAccess) {
        Optional<? extends Registry<T>> vanillaRegistry = registryAccess.registry(tagKey.registry());
        return vanillaRegistry.map(registry -> isVanillaObjectTaggedAs(registry, object, tagKey)).orElseGet(() -> isForgeObjectTaggedAs(object, tagKey));
    }
}
