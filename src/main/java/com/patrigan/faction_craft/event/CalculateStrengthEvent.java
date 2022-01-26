package com.patrigan.faction_craft.event;

import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.raid.target.RaidTarget;
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

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public ServerWorld getLevel() {
        return level;
    }

    public int getOriginalStrength() {
        return originalStrength;
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

    public static class FactionBattle extends CalculateStrengthEvent
    {
        private final Faction faction1;
        private final Faction faction2;

        public FactionBattle(RaidTarget.Type type, BlockPos blockPos, ServerWorld level, int originalStrength, int strength, Faction faction1, Faction faction2) {
            super(type, blockPos, level, originalStrength, strength);
            this.faction1 = faction1;
            this.faction2 = faction2;
        }

        public Faction getFaction1() {
            return faction1;
        }

        public Faction getFaction2() {
            return faction2;
        }
    }
}
