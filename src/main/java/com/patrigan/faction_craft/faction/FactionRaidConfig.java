package com.patrigan.faction_craft.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class FactionRaidConfig {
    public static final float DEFAULT_MOBS_FRACTION = 0.7F;
    public static final FactionRaidConfig DEFAULT = new FactionRaidConfig(DEFAULT_MOBS_FRACTION);

    public static final Codec<FactionRaidConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.FLOAT.optionalFieldOf("mobs_fraction", DEFAULT_MOBS_FRACTION).forGetter(FactionRaidConfig::getMobsFraction)
            ).apply(builder, FactionRaidConfig::new));

    private final float mobsFraction;

    public float getMobsFraction() {
        return mobsFraction;
    }

    public FactionRaidConfig(float mobsFraction) {
        this.mobsFraction = mobsFraction;
    }
}
