package com.patrigan.faction_craft.util;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class GeneralUtils {
    public static <E> Codec<Either<TagKey<E>, List<E>>> getRegistryCodec(ResourceKey<? extends Registry<E>> resourceKey){
        return Codec.either(TagKey.hashedCodec(resourceKey), ((Registry<E>) BuiltinRegistries.REGISTRY.get(resourceKey.location())).byNameCodec().listOf());
    }


    // Weighted RandomSource from: https://stackoverflow.com/a/6737362
    public static <T> T getRandomEntry(List<Pair<T, Integer>> rlList, RandomSource random) {
        double totalWeight = 0.0;

        // Compute the total weight of all items together.
        for (Pair<T, Integer> pair : rlList) {
            totalWeight += pair.getSecond();
        }

        // Now choose a random item.
        int index = 0;
        for (double randomWeightPicked = random.nextFloat() * totalWeight; index < rlList.size() - 1; ++index) {
            randomWeightPicked -= rlList.get(index).getSecond();
            if (randomWeightPicked <= 0.0) break;
        }

        return rlList.get(index).getFirst();
    }

    public static <T> T getRandomItem(List<T> givenList, RandomSource random) {
        if(givenList.isEmpty()){
            return null;
        }
        return givenList.get(random.nextInt(givenList.size()));
    }

    public static BlockPos vec3ToBlockPos(Vec3 vec3) {
        return new BlockPos(vec3.x, vec3.y, vec3.z);
    }

    public static Vec3 blockPosToVec3(BlockPos blockPos) {
        return new Vec3(blockPos.getX()+0.5D, blockPos.getY(), blockPos.getZ()+0.5D);
    }
}
