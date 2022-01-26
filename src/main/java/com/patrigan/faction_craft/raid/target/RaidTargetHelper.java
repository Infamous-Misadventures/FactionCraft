package com.patrigan.faction_craft.raid.target;

import com.patrigan.faction_craft.faction.Factions;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class RaidTargetHelper {

    public static RaidTarget load(ServerWorld level, CompoundNBT compoundNBT){
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

    private static RaidTarget loadFactionBattleRaidTarget(CompoundNBT compoundNBT){
        return new FactionBattleRaidTarget(
                compoundNBT.getInt("TargetStrength"),
                new BlockPos(compoundNBT.getInt("X"), compoundNBT.getInt("Y"), compoundNBT.getInt("Z")),
                Factions.getFaction(new ResourceLocation(compoundNBT.getString("Faction1"))),
                Factions.getFaction(new ResourceLocation(compoundNBT.getString("Faction2")))
        );
    }
}
