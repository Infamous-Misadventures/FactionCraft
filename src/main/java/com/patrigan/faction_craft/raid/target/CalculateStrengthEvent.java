package com.patrigan.faction_craft.raid.target;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

public class CalculateStrengthEvent extends Event {

    RaidTarget.Type type;
    BlockPos blockPos;
    ServerWorld level;
    int originalStrength;
    int strength;

    public CalculateStrengthEvent(RaidTarget.Type type, BlockPos blockPos, ServerWorld level, int originalStrength, int strength) {
        this.type = type;
        this.blockPos = blockPos;
        this.level = level;
        this.originalStrength = originalStrength;
        this.strength = strength;
    }

    public RaidTarget.Type getType() {
        return type;
    }

    public void setType(RaidTarget.Type type) {
        this.type = type;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public ServerWorld getLevel() {
        return level;
    }

    public void setLevel(ServerWorld level) {
        this.level = level;
    }

    public int getOriginalStrength() {
        return originalStrength;
    }

    public void setOriginalStrength(int originalStrength) {
        this.originalStrength = originalStrength;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public static class Player extends CalculateStrengthEvent
    {
        ServerPlayerEntity player;

        public Player(RaidTarget.Type type, ServerPlayerEntity player, ServerWorld level, int originalStrength, int strength) {
            super(type, player.blockPosition(), level, originalStrength, strength);
            this.player = player;
        }

        public ServerPlayerEntity getPlayer() {
            return player;
        }
    }
}
