package com.patrigan.faction_craft.raid.target;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class RaidTargetHelper {

    public static RaidTarget load(ServerWorld level, CompoundNBT compoundNBT){
        RaidTarget.Type type = RaidTarget.Type.getByName(compoundNBT.getString("Type"));
        if (type == RaidTarget.Type.VILLAGE) {
            return loadVillageRaidTarget(compoundNBT);
        }else if(type == RaidTarget.Type.PLAYER){
            return loadPlayerRaidTarget(level, compoundNBT);
        }
        return null;
    }

    private static RaidTarget loadPlayerRaidTarget(ServerWorld level, CompoundNBT compoundNBT) {
        return new PlayerRaidTarget(
                level.players().stream().filter(serverPlayerEntity -> serverPlayerEntity.getStringUUID().equals(compoundNBT.getString("Player"))).findFirst().get(),
                compoundNBT.getInt("TargetStrength")
        );
    }

    private static RaidTarget loadVillageRaidTarget(CompoundNBT compoundNBT){
        return new VillageRaidTarget(
                new BlockPos(compoundNBT.getInt("X"), compoundNBT.getInt("Y"), compoundNBT.getInt("Z")),
                compoundNBT.getInt("TargetStrength")
        );
    }
}
