package com.patrigan.faction_craft.raid.target;

import com.patrigan.faction_craft.faction.Factions;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class RaidTargetHelper {

    public static RaidTarget load(ServerLevel level, CompoundTag compoundNBT){
        RaidTarget.Type type = RaidTarget.Type.byName(compoundNBT.getString("Type"), RaidTarget.Type.VILLAGE);
        if (type == RaidTarget.Type.VILLAGE) {
            return loadVillageRaidTarget(compoundNBT);
        }else if(type == RaidTarget.Type.PLAYER){
            return loadPlayerRaidTarget(level, compoundNBT);
        }else if(type == RaidTarget.Type.BATTLE){
            return loadFactionBattleRaidTarget(compoundNBT);
        }
        return null;
    }

    private static RaidTarget loadPlayerRaidTarget(ServerLevel level, CompoundTag compoundNBT) {
        return new PlayerRaidTarget(
                level.players().stream().filter(serverPlayerEntity -> serverPlayerEntity.getStringUUID().equals(compoundNBT.getString("Player"))).findFirst().get(),
                compoundNBT.getInt("TargetStrength")
        );
    }

    private static RaidTarget loadVillageRaidTarget(CompoundTag compoundNBT){
        return new VillageRaidTarget(
                new BlockPos(compoundNBT.getInt("X"), compoundNBT.getInt("Y"), compoundNBT.getInt("Z")),
                compoundNBT.getInt("TargetStrength")
        );
    }

    private static RaidTarget loadFactionBattleRaidTarget(CompoundTag compoundNBT){
        return new FactionBattleRaidTarget(
                compoundNBT.getInt("TargetStrength"),
                new BlockPos(compoundNBT.getInt("X"), compoundNBT.getInt("Y"), compoundNBT.getInt("Z")),
                Factions.getFaction(new ResourceLocation(compoundNBT.getString("Faction1"))),
                Factions.getFaction(new ResourceLocation(compoundNBT.getString("Faction2")))
        );
    }
}
