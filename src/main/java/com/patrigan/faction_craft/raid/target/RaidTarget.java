package com.patrigan.faction_craft.raid.target;

import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

import java.util.Locale;

public interface RaidTarget {

    BlockPos getTargetBlockPos();

    void updateTargetBlockPos(ServerWorld level);

    int getTargetStrength();

    int getAdditionalWaves();

    boolean checkLossCondition(ServerWorld level);

    CompoundNBT save(CompoundNBT compoundNbt);

    boolean isValidSpawnPos(int outerAttempt, BlockPos.Mutable blockpos$mutable, ServerWorld level);

    enum Type{
        VILLAGE,
        PLAYER;
        private static final Type[] VALUES = values();
        public static Type getByName(String pName) {
            for(Type raid$status : VALUES) {
                if (pName.equalsIgnoreCase(raid$status.name())) {
                    return raid$status;
                }
            }
            return VILLAGE;
        }
        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
