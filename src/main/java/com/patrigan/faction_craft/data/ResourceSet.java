package com.patrigan.faction_craft.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.patrigan.faction_craft.tags.TagHelper;
import com.patrigan.faction_craft.util.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceSet<T> {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final List<TagOrElementLocation> resourceLocations;
    private List<TagKey<T>> tags;
    private List<T> objects;
    public ResourceSet(ResourceKey<? extends Registry<T>> registryKey, List<TagOrElementLocation> resourceLocations) {
        this.registryKey = registryKey;
        this.resourceLocations = resourceLocations;
    }
    public record TagOrElementLocation(ResourceLocation id, boolean tag) {
        public String toString() {
            return this.decoratedId();
        }

        private String decoratedId() {
            return this.tag ? this.id.getNamespace()+":#" + this.id.getPath() : this.id.toString();
        }
    }

    public static <T> Codec<ResourceSet<T>> getCodec(ResourceKey<? extends Registry<T>> registryKey) {
        return TAG_OR_ELEMENT_ID.listOf().comapFlatMap((p_216169_) -> DataResult.success(new ResourceSet<>(registryKey, p_216169_)), data -> data.resourceLocations);
    }

    public static final Codec<TagOrElementLocation> TAG_OR_ELEMENT_ID = Codec.STRING.comapFlatMap((p_216169_) -> {
        return p_216169_.contains("#") ? ResourceLocation.read(p_216169_.replace("#", "")).map((p_216182_) -> {
            return new TagOrElementLocation(p_216182_, true);
        }) : ResourceLocation.read(p_216169_).map((p_216165_) -> {
            return new TagOrElementLocation(p_216165_, false);
        });
    }, TagOrElementLocation::decoratedId);

    public static <T> ResourceSet<T> getEmpty(ResourceKey<? extends Registry<T>> registryKey) {
        return new ResourceSet<>(registryKey, new ArrayList<>());
    }

    public boolean contains(T object) {
        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        if(currentServer == null) {
            return false;
        }
        return this.getObjects().contains(object) || this.getTags().stream().anyMatch(tag -> TagHelper.isTaggedAs(object, tag, currentServer.registryAccess()));
    }

    private List<T> getObjects() {
        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        if (this.objects == null && currentServer != null) {
            this.objects = this.resourceLocations.stream()
                    .filter(resourceLocation -> !resourceLocation.tag())
                    .map(resourceLocation -> RegistryHelper.loadFromRegistry(resourceLocation.id(), registryKey, currentServer.registryAccess()))
                    .collect(Collectors.toList());
        }
        return objects;
    }

    private List<TagKey<T>> getTags() {
        if (this.tags == null) {
            this.tags = this.resourceLocations.stream()
                    .filter(TagOrElementLocation::tag)
                    .map(resourceLocation -> TagKey.create(this.registryKey, resourceLocation.id()))
                    .collect(Collectors.toList());
        }
        return tags;
    }

    public boolean isEmpty() {
        return this.resourceLocations.isEmpty();
    }

    public ResourceSet<T> merge(ResourceSet<T> other) {
        List<TagOrElementLocation> newResourceLocations = new ArrayList<>(this.resourceLocations);
        newResourceLocations.addAll(other.resourceLocations);
        return new ResourceSet<>(this.registryKey, newResourceLocations);
    }
}
