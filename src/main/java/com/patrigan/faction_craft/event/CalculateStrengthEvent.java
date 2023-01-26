package com.patrigan.faction_craft.event;

import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.raid.target.RaidTarget;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.Event;

public class CalculateStrengthEvent extends Event {
    RaidTarget.Type type;
    BlockPos blockPos;
    ServerLevel level;
    int originalStrength;
    int strength;

    public CalculateStrengthEvent(RaidTarget.Type type, BlockPos blockPos, ServerLevel level, int originalStrength, int strength) {
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

    public ServerLevel getLevel() {
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
        ServerPlayer player;

        public Player(RaidTarget.Type type, ServerPlayer player, ServerLevel level, int originalStrength, int strength) {
            super(type, player.blockPosition(), level, originalStrength, strength);
            this.player = player;
        }

        public ServerPlayer getPlayer() {
            return player;
        }
    }

    public static class FactionBattle extends CalculateStrengthEvent
    {
        private final Faction faction1;
        private final Faction faction2;

        public FactionBattle(RaidTarget.Type type, BlockPos blockPos, ServerLevel level, int originalStrength, int strength, Faction faction1, Faction faction2) {
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
