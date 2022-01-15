package com.patrigan.faction_craft.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class FactionRaidConfig {
    public static final float DEFAULT_MOBS_FRACTION = 0.7F;
    public static final FactionRaidConfig DEFAULT = new FactionRaidConfig("event.minecraft.raid", DEFAULT_MOBS_FRACTION);

    public static final Codec<FactionRaidConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.STRING.optionalFieldOf("name", "event.minecraft.raid").forGetter(FactionRaidConfig::getName),
                    Codec.FLOAT.optionalFieldOf("mobs_fraction", DEFAULT_MOBS_FRACTION).forGetter(FactionRaidConfig::getMobsFraction)
            ).apply(builder, FactionRaidConfig::new));

    private final String name;
    private final float mobsFraction;

    public FactionRaidConfig(String name, float mobsFraction) {
        this.name = name;
        this.mobsFraction = mobsFraction;
    }

    public String getName() {
        return name;
    }

    public float getMobsFraction() {
        return mobsFraction;
    }
}
