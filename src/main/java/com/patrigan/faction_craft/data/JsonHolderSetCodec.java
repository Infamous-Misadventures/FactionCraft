package com.patrigan.faction_craft.data;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class JsonHolderSetCodec<E> implements Codec<HolderSet<E>> {
    private final ResourceKey<? extends Registry<E>> registryKey;
    private final Codec<Holder<E>> elementCodec;
    private final Codec<List<Holder<E>>> homogenousListCodec;
    private final Codec<Either<TagKey<E>, List<Holder<E>>>> registryAwareCodec;
    private final Codec<net.minecraftforge.registries.holdersets.ICustomHolderSet<E>> forgeDispatchCodec;
    private final Codec<Either<net.minecraftforge.registries.holdersets.ICustomHolderSet<E>, Either<TagKey<E>, List<Holder<E>>>>> combinedCodec;

    private JsonHolderSetCodec(ResourceKey<? extends Registry<E>> p_206660_, boolean p_206662_) {
        this.registryKey = p_206660_;
        Registry<E> registry = getCorrectRegistry(p_206660_);
        this.elementCodec = registry.holderByNameCodec();
        this.homogenousListCodec = homogenousList(elementCodec, p_206662_);
        this.registryAwareCodec = Codec.either(TagKey.hashedCodec(p_206660_), this.homogenousListCodec);
        // FORGE: make registry-specific dispatch codec and make forge-or-vanilla either codec
        this.forgeDispatchCodec = ExtraCodecs.lazyInitializedCodec(() -> net.minecraftforge.registries.ForgeRegistries.HOLDER_SET_TYPES.get().getCodec())
                .dispatch(net.minecraftforge.registries.holdersets.ICustomHolderSet::type, type -> type.makeCodec(p_206660_, this.elementCodec, p_206662_));
        this.combinedCodec = new ExtraCodecs.EitherCodec<>(this.forgeDispatchCodec, this.registryAwareCodec);
    }

    @Nullable
    private Registry<E> getCorrectRegistry(ResourceKey<? extends Registry<E>> resourceKey) {
        Registry<E> registry = (Registry<E>) BuiltinRegistries.REGISTRY.get(resourceKey.location());
        if(registry == null){
            registry = (Registry<E>) Registry.REGISTRY.get(resourceKey.location());
        }
        return registry;
    }

    private static <E> Codec<List<Holder<E>>> homogenousList(Codec<Holder<E>> pHolderCodec, boolean pDisallowInline) {
        Function<List<Holder<E>>, DataResult<List<Holder<E>>>> function = ExtraCodecs.ensureHomogenous(Holder::kind);
        Codec<List<Holder<E>>> codec = pHolderCodec.listOf().flatXmap(function, function);
        return pDisallowInline ? codec : Codec.either(codec, pHolderCodec).xmap((p_206664_) -> {
            return p_206664_.map((p_206694_) -> {
                return p_206694_;
            }, List::of);
        }, (p_206684_) -> {
            return p_206684_.size() == 1 ? Either.right(p_206684_.get(0)) : Either.left(p_206684_);
        });
    }

    public static <E> Codec<HolderSet<E>> create(ResourceKey<? extends Registry<E>> pRegistryKey, boolean pDisallowInline) {
        return new JsonHolderSetCodec<>(pRegistryKey, pDisallowInline);
    }

    public <T> DataResult<Pair<HolderSet<E>, T>> decode(DynamicOps<T> p_206696_, T p_206697_) {
        Registry<E> registry = getCorrectRegistry(registryKey);
        // FORGE: use the wrapped codec to decode custom/tag/list instead of just tag/list
        return this.combinedCodec.decode(p_206696_, p_206697_).map((p_206682_) -> {
            return p_206682_.mapFirst((p_206679_) -> {
                return p_206679_.map(Function.identity(), tagOrList -> tagOrList.map(registry::getOrCreateTag, HolderSet::direct));
            });
        });
    }

    public <T> DataResult<T> encode(HolderSet<E> p_206674_, DynamicOps<T> p_206675_, T p_206676_) {
        Registry<E> registry = (Registry<E>) BuiltinRegistries.REGISTRY.get(registryKey.location());
        if (!p_206674_.isValidInRegistry(registry)) {
            return DataResult.error("HolderSet " + p_206674_ + " is not valid in current registry set");
        }

        // FORGE: use the dispatch codec to encode custom holdersets, otherwise fall back to vanilla tag/list
        if (p_206674_ instanceof net.minecraftforge.registries.holdersets.ICustomHolderSet<E> customHolderSet)
            return this.forgeDispatchCodec.encode(customHolderSet, p_206675_, p_206676_);
        return this.registryAwareCodec.encode(p_206674_.unwrap().mapRight(List::copyOf), p_206675_, p_206676_);

    }

}
