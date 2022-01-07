package com.patrigan.faction_craft.raid.target;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class RaidTargetHelper {

    public static RaidTarget load(CompoundNBT compoundNBT){
        RaidTarget.Type type = RaidTarget.Type.getByName(compoundNBT.getString("Type"));
        if (type == RaidTarget.Type.VILLAGE) {
            return loadVillageRaidTarget(compoundNBT);
        }
        return null;
    }

    private static RaidTarget loadVillageRaidTarget(CompoundNBT compoundNBT){
        return new VillageRaidTarget(
                new BlockPos(compoundNBT.getInt("X"), compoundNBT.getInt("Y"), compoundNBT.getInt("Z")),
                compoundNBT.getInt("TargetStrength")
        );
    }
}
