package com.patrigan.faction_craft.raid.target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IExtensibleEnum;

public interface RaidTarget {

    BlockPos getTargetBlockPos();

    void updateTargetBlockPos(ServerWorld level);

    int getTargetStrength();

    int getAdditionalWaves();

    boolean checkLossCondition(Raid raid, ServerWorld level);

    CompoundNBT save(CompoundNBT compoundNbt);

    boolean isValidSpawnPos(int outerAttempt, BlockPos.Mutable blockpos$mutable, ServerWorld level);

    Type getRaidType();

    int getStartingWave();

    enum Type implements IExtensibleEnum {
        VILLAGE("village"),
        PLAYER("player"),
        BATTLE("battle");
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

        public static RaidTarget.Type create(String id, String name)
        {
            throw new IllegalStateException("Enum not extended");
        }
    }
}
