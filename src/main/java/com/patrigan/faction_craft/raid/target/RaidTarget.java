package com.patrigan.faction_craft.raid.target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.common.IExtensibleEnum;

import java.util.Locale;

public interface RaidTarget {

    BlockPos getTargetBlockPos();

    void updateTargetBlockPos(ServerWorld level);

    int getTargetStrength();

    int getAdditionalWaves();

    boolean checkLossCondition(ServerWorld level);

    CompoundNBT save(CompoundNBT compoundNbt);

    boolean isValidSpawnPos(int outerAttempt, BlockPos.Mutable blockpos$mutable, ServerWorld level);

    enum Type implements IExtensibleEnum {
        VILLAGE("village"),
        PLAYER("player");
        public static final Codec<RaidTarget.Type> CODEC = Codec.STRING.flatComapMap(s -> RaidTarget.Type.byName(s, null), d -> DataResult.success(d.getName()));

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static RaidTarget.Type byName(String key, RaidTarget.Type fallBack) {
            for(RaidTarget.Type raidTargetType : values()) {
                if (raidTargetType.name.equals(key)) {
                    return raidTargetType;
                }
            }

            return fallBack;
        }

        public static RaidTarget.Type create(String id, String name, int max)
        {
            throw new IllegalStateException("Enum not extended");
        }
    }
}
