package com.patrigan.faction_craft.raid.target;

import com.patrigan.faction_craft.FactionCraft;
import com.patrigan.faction_craft.event.CalculateStrengthEvent;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.NaturalSpawner;

import static com.patrigan.faction_craft.config.FactionCraftConfig.*;
import static com.patrigan.faction_craft.raid.target.RaidTarget.Type.PLAYER;
import static com.patrigan.faction_craft.raid.target.RaidTarget.Type.VILLAGE;

public class PlayerRaidTarget implements RaidTarget {

    private final Type raidType = VILLAGE;
    private final ServerPlayer player;
    private final int targetStrength;

    public PlayerRaidTarget(ServerPlayer player, ServerLevel level) {
        this.player = player;
        this.targetStrength = calculateTargetStrength(player, level);
    }

    private int calculateTargetStrength(ServerPlayer player, ServerLevel level) {
        int strength = PLAYER_RAID_TARGET_BASE_STRENGTH.get();
        CalculateStrengthEvent event = new CalculateStrengthEvent.Player(PLAYER, player, level, strength, strength);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        FactionCraft.LOGGER.info("Strength = " + strength);
        return (int) Math.floor(event.getStrength()*PLAYER_RAID_TARGET_STRENGTH_MULTIPLIER.get());
    }

    public PlayerRaidTarget(ServerPlayer player, int targetStrength) {
        this.player = player;
        this.targetStrength = targetStrength;
    }

    @Override
    public BlockPos getTargetBlockPos() {
        return player.blockPosition();
    }

    @Override
    public void updateTargetBlockPos(ServerLevel level) {
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
    public boolean checkLossCondition(Raid raid, ServerLevel level) {
        return !player.isAlive();
    }

    @Override
    public boolean isValidSpawnPos(int outerAttempt, BlockPos.MutableBlockPos blockpos$mutable, ServerLevel level) {
        return (blockpos$mutable.distSqr(player.blockPosition()) > 30 || outerAttempt >= 2)
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
    public CompoundTag save(CompoundTag compoundNbt){
        compoundNbt.putString("Type", this.raidType.getName());
        compoundNbt.putString("Player", player.getStringUUID());
        compoundNbt.putInt("TargetStrength", targetStrength);
        return compoundNbt;
    }
}
