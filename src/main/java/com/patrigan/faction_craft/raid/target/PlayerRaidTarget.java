package com.patrigan.faction_craft.raid.target;

import com.patrigan.faction_craft.FactionCraft;
import com.patrigan.faction_craft.event.CalculateStrengthEvent;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;

import static com.patrigan.faction_craft.config.FactionCraftConfig.*;
import static com.patrigan.faction_craft.raid.target.RaidTarget.Type.PLAYER;
import static com.patrigan.faction_craft.raid.target.RaidTarget.Type.VILLAGE;

public class PlayerRaidTarget implements RaidTarget {

    private final Type raidType = VILLAGE;
    private ServerPlayerEntity player;
    private int targetStrength;

    public PlayerRaidTarget(ServerPlayerEntity player, ServerWorld level) {
        this.player = player;
        this.targetStrength = calculateTargetStrength(player, level);
    }

    private int calculateTargetStrength(ServerPlayerEntity player, ServerWorld level) {
        int strength = PLAYER_RAID_TARGET_BASE_STRENGTH.get();
        CalculateStrengthEvent event = new CalculateStrengthEvent.Player(PLAYER, player, level, strength, strength);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        FactionCraft.LOGGER.info("Strength = " + strength);
        return (int) Math.floor(event.getStrength()*PLAYER_RAID_TARGET_STRENGTH_MULTIPLIER.get());
    }

    public PlayerRaidTarget(ServerPlayerEntity player, int targetStrength) {
        this.player = player;
        this.targetStrength = targetStrength;
    }

    @Override
    public BlockPos getTargetBlockPos() {
        return player.blockPosition();
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
        return !player.isAlive();
    }

    @Override
    public boolean isValidSpawnPos(int outerAttempt, BlockPos.Mutable blockpos$mutable, ServerWorld level) {
        return (blockpos$mutable.distSqr(player.blockPosition()) > 30 || outerAttempt >= 2)
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
        compoundNbt.putString("Player", player.getStringUUID());
        compoundNbt.putInt("TargetStrength", targetStrength);
        return compoundNbt;
    }
}
