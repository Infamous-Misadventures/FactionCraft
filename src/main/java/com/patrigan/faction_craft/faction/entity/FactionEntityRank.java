package com.patrigan.faction_craft.faction.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum FactionEntityRank {
    LEADER("leader", null),
    SUPPORT("support", null),
    MOUNT("mount", null),
    DIGGER("digger", null),
    GENERAL("general", LEADER),
    CAPTAIN("captain", GENERAL),
    SOLDIER("soldier", CAPTAIN);

    public static final Codec<FactionEntityRank> CODEC = Codec.STRING.flatComapMap(s -> FactionEntityRank.byName(s, null), d -> DataResult.success(d.getName()));

    private final String name;
    private final FactionEntityRank promotion;

    FactionEntityRank(String name, FactionEntityRank promotion) {
        this.name = name;
        this.promotion = promotion;
    }

    public static FactionEntityRank byName(String name, FactionEntityRank defaultRank) {
        for (FactionEntityRank factionEntityRank : values()){
            if (factionEntityRank.name.equalsIgnoreCase(name)) {
                return factionEntityRank;
            }
        }

        return defaultRank;
    }

    public FactionEntityRank promote() {
        return promotion;
    }

    public String getName() {
        return name;
    }
}
