package com.patrigan.faction_craft.raid.target;

import com.patrigan.faction_craft.FactionCraft;
import com.patrigan.faction_craft.event.CalculateStrengthEvent;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.stream.Stream;

import static com.patrigan.faction_craft.config.FactionCraftConfig.*;
import static com.patrigan.faction_craft.raid.target.RaidTarget.Type.VILLAGE;

public class VillageRaidTarget implements RaidTarget {

    private final RaidTarget.Type raidType = VILLAGE;
    private BlockPos blockPos;
    private int targetStrength;

    public VillageRaidTarget(BlockPos blockPos, ServerLevel level) {
        this.blockPos = blockPos;
        updateTargetBlockPos(level);
        this.targetStrength = calculateTargetStrength(blockPos, level);
    }

    private int calculateTargetStrength(BlockPos blockPos, ServerLevel level) {
        int strength = 0;
        strength += level.getEntitiesOfClass(AbstractVillager.class,
                new AABB(blockPos).inflate(100),
                abstractVillagerEntity -> true).size() * VILLAGE_RAID_VILLAGER_WEIGHT.get();
        strength += level.getEntitiesOfClass(IronGolem.class,
                new AABB(blockPos).inflate(100),
                ironGolemEntity -> true).size() * VILLAGE_RAID_IRON_GOLEM_WEIGHT.get();
        CalculateStrengthEvent event = new CalculateStrengthEvent(VILLAGE, blockPos, level, strength, strength);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
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
    public void updateTargetBlockPos(ServerLevel level) {
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
    public void increaseTargetStrength(int amount) {
        this.targetStrength += amount;
    }

    @Override
    public int getAdditionalWaves() {
        return (int) Math.floor(VILLAGE_RAID_ADDITIONAL_WAVE_CHANCE.get()*targetStrength);
    }

    @Override
    public boolean isDefeat(Raid raid, ServerLevel level) {
        if(level.getGameTime() % 20 == 0) {
            if (level.getEntitiesOfClass(AbstractVillager.class,
                    new AABB(blockPos).inflate(100),
                    abstractVillagerEntity -> !abstractVillagerEntity.isBaby()).size() == 0) {
                return true;
            }
        }
        return !level.isVillage(blockPos);
    }

    @Override
    public boolean isValidSpawnPos(int outerAttempt, BlockPos.MutableBlockPos blockpos$mutable, ServerLevel level) {
        return (!level.isVillage(blockpos$mutable) || outerAttempt >= 2)
                && level.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10)
                && level.isPositionEntityTicking(blockpos$mutable)
                && (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, level, blockpos$mutable, EntityType.RAVAGER)
                    || level.getBlockState(blockpos$mutable.below()).is(Blocks.SNOW) && level.getBlockState(blockpos$mutable).isAir());
    }

    @Override
    public Type getRaidType() {
        return raidType;
    }

    @Override
    public int getStartingWave() {
        return 0;
    }

    @Override
    public float getSpawnDistance() {
        return 32.0F;
    }

    private void moveRaidCenterToNearbyVillageSection(ServerLevel level) {
        Stream<SectionPos> stream = SectionPos.cube(SectionPos.of(blockPos), 2);
        stream.filter(level::isVillage).map(SectionPos::center).min(Comparator.comparingDouble((p_223025_1_) -> {
            return p_223025_1_.distSqr(blockPos);
        })).ifPresent(this::setBlockPos);
    }

    @Override
    public CompoundTag save(CompoundTag compoundNbt){
        compoundNbt.putString("Type", this.raidType.getName());
        compoundNbt.putInt("X", blockPos.getX());
        compoundNbt.putInt("Y", blockPos.getY());
        compoundNbt.putInt("Z", blockPos.getZ());
        compoundNbt.putInt("TargetStrength", targetStrength);
        return compoundNbt;
    }
}
