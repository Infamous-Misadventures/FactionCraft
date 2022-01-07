package com.patrigan.faction_craft.raid.target;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Locale;

public interface RaidTarget {

    BlockPos getTargetBlockPos();

    void updateTargetBlockPos(ServerWorld level);

    int getTargetStrength();

    int getAdditionalWaves();

    boolean checkLossCondition(ServerWorld level);

    CompoundNBT save(CompoundNBT compoundNbt);

    enum Type{
        VILLAGE;
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
