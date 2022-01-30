package com.patrigan.faction_craft.raid.target;

import com.patrigan.faction_craft.FactionCraft;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.event.CalculateStrengthEvent;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

import java.util.stream.Collectors;

import static com.patrigan.faction_craft.config.FactionCraftConfig.*;
import static com.patrigan.faction_craft.raid.target.RaidTarget.Type.*;

public class FactionBattleRaidTarget implements RaidTarget {

    private final Type raidType = BATTLE;
    private int targetStrength;
    private BlockPos targetBlockPos;
    private final Faction faction1;
    private final Faction faction2;

    public FactionBattleRaidTarget(BlockPos targetBlockPos, Faction faction1, Faction faction2, ServerWorld level) {
        this.targetBlockPos = targetBlockPos;
        this.faction1 = faction1;
        this.faction2 = faction2;
        this.targetStrength = calculateTargetStrength(level);
    }

    private int calculateTargetStrength(ServerWorld level) {
        int strength = FACTION_BATTLE_RAID_TARGET_BASE_STRENGTH.get();
        CalculateStrengthEvent event = new CalculateStrengthEvent.FactionBattle(BATTLE, targetBlockPos, level, strength, strength, faction1, faction2);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        FactionCraft.LOGGER.info("Strength = " + strength);
        return (int) Math.floor(event.getStrength()*FACTION_BATTLE_RAID_TARGET_STRENGTH_MULTIPLIER.get());
    }

    public FactionBattleRaidTarget(int targetStrength, BlockPos targetBlockPos, Faction faction1, Faction faction2) {
        this.targetStrength = targetStrength;
        this.targetBlockPos = targetBlockPos;
        this.faction1 = faction1;
        this.faction2 = faction2;
    }

    @Override
    public BlockPos getTargetBlockPos() {
        return this.targetBlockPos;
    }

    @Override
    public void updateTargetBlockPos(ServerWorld level) {
        // noop
    }

    @Override
    public int getTargetStrength() {
        return targetStrength;
    }

    @Override
    public int getAdditionalWaves() {
        return (int) Math.floor(VILLAGE_RAID_ADDITIONAL_WAVE_CHANCE.get()*targetStrength);
    }

    @Override
    public boolean checkLossCondition(Raid raid, ServerWorld level) {
        if(raid.getGroupsSpawned() == 0){
            return false;
        }
        return raid.getRaidersInWave(raid.getGroupsSpawned()).stream().map(mobEntity -> FactionEntityHelper.getFactionEntityCapability(mobEntity).getFaction()).collect(Collectors.toSet()).size()<=1;
    }

    @Override
    public boolean isValidSpawnPos(int outerAttempt, BlockPos.Mutable blockpos$mutable, ServerWorld level) {
        return (blockpos$mutable.distSqr(targetBlockPos) > 30 || outerAttempt >= 2)
                && level.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10)
                && level.getChunkSource().isEntityTickingChunk(new ChunkPos(blockpos$mutable))
                && (WorldEntitySpawner.isSpawnPositionOk(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, level, blockpos$mutable, EntityType.RAVAGER)
                || level.getBlockState(blockpos$mutable.below()).is(Blocks.SNOW) && level.getBlockState(blockpos$mutable).isAir());
    }

    @Override
    public Type getRaidType() {
        return raidType;
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNbt){
        compoundNbt.putString("Type", this.raidType.getName());
        compoundNbt.putInt("X", targetBlockPos.getX());
        compoundNbt.putInt("Y", targetBlockPos.getY());
        compoundNbt.putInt("Z", targetBlockPos.getZ());
        compoundNbt.putInt("TargetStrength", targetStrength);
        compoundNbt.putString("Faction1", faction1.getName().toString());
        compoundNbt.putString("Faction2", faction2.getName().toString());
        return compoundNbt;
    }
}
