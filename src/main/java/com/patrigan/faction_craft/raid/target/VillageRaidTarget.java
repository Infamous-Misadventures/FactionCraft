package com.patrigan.faction_craft.raid.target;

import com.patrigan.faction_craft.FactionCraft;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

import java.util.Comparator;
import java.util.stream.Stream;

import static com.patrigan.faction_craft.config.FactionCraftConfig.*;
import static com.patrigan.faction_craft.raid.target.RaidTarget.Type.VILLAGE;

public class VillageRaidTarget implements RaidTarget {

    private final RaidTarget.Type boostType = VILLAGE;
    private BlockPos blockPos;
    private int targetStrength;

    public VillageRaidTarget(BlockPos blockPos, ServerWorld level) {
        this.blockPos = blockPos;
        updateTargetBlockPos(level);
        this.targetStrength = calculateTargetStrength(blockPos, level);
    }

    private int calculateTargetStrength(BlockPos blockPos, ServerWorld level) {
        int strength = 0;
        strength += level.getLoadedEntitiesOfClass(AbstractVillagerEntity.class,
                new AxisAlignedBB(blockPos).inflate(100),
                abstractVillagerEntity -> true).size() * VILLAGE_RAID_VILLAGER_WEIGHT.get();
        strength += level.getLoadedEntitiesOfClass(IronGolemEntity.class,
                new AxisAlignedBB(blockPos).inflate(100),
                ironGolemEntity -> true).size() * VILLAGE_RAID_IRON_GOLEM_WEIGHT.get();
        CalculateStrengthEvent event = new CalculateStrengthEvent(VILLAGE, blockPos, level, strength, strength);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        FactionCraft.LOGGER.info("Strength = " + strength);
        return (int) Math.floor(event.getStrength()*VILLAGE_RAID_TARGET_STRENGTH_MULTIPLIER.get());
    }

    public VillageRaidTarget(BlockPos blockPos, int targetStrength) {
        this.blockPos = blockPos;
        this.targetStrength = targetStrength;
    }

    @Override
    public BlockPos getTargetBlockPos() {
        return blockPos;
    }

    @Override
    public void updateTargetBlockPos(ServerWorld level) {
        if (!level.isVillage(blockPos)) {
            this.moveRaidCenterToNearbyVillageSection(level);
        }
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
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
    public boolean checkLossCondition(ServerWorld level) {
        return !level.isVillage(blockPos);
    }

    @Override
    public boolean isValidSpawnPos(int outerAttempt, BlockPos.Mutable blockpos$mutable, ServerWorld level) {
        return (!level.isVillage(blockpos$mutable) || outerAttempt >= 2)
                && level.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10)
                && level.getChunkSource().isEntityTickingChunk(new ChunkPos(blockpos$mutable))
                && (WorldEntitySpawner.isSpawnPositionOk(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, level, blockpos$mutable, EntityType.RAVAGER)
                    || level.getBlockState(blockpos$mutable.below()).is(Blocks.SNOW) && level.getBlockState(blockpos$mutable).isAir());
    }

    private void moveRaidCenterToNearbyVillageSection(ServerWorld level) {
        Stream<SectionPos> stream = SectionPos.cube(SectionPos.of(blockPos), 2);
        stream.filter(level::isVillage).map(SectionPos::center).min(Comparator.comparingDouble((p_223025_1_) -> {
            return p_223025_1_.distSqr(blockPos);
        })).ifPresent(this::setBlockPos);
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNbt){
        compoundNbt.putString("Type", this.boostType.getName());
        compoundNbt.putInt("X", blockPos.getX());
        compoundNbt.putInt("Y", blockPos.getY());
        compoundNbt.putInt("Z", blockPos.getZ());
        compoundNbt.putInt("TargetStrength", targetStrength);
        return compoundNbt;
    }
}
