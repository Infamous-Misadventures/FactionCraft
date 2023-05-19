package com.patrigan.faction_craft.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum FactionType {
    PLAYER("player"),
    VILLAGE("village"),
    GAIA("gaia"),
    MONSTER("monster");
    public static final Codec<FactionType> CODEC = Codec.STRING.flatComapMap(s -> FactionType.byName(s, null), d -> DataResult.success(d.getName()));

    private final String name;

    FactionType(String name) {
        this.name = name;
    }

    public static FactionType byName(String key, FactionType fallBack) {
        for (FactionType factionType : values()) {
            if (factionType.name.equals(key)) {
                return factionType;
            }
        }

        return fallBack;
    }

    public String getName() {
        return name;
    }
}